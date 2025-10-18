package com.xdai.rag.chatstorage.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static final Logger log = LoggerFactory.getLogger(AttributeEncryptor.class);
    private static final String ENV_KEY_NAME = "qzExnoGTjyWI0hSCb+azbX9tp5OVh0ADvQ1Ox/8IUwI="; // Generated from 'head -c 32 /dev/urandom | base64'
    private static final String ALGO = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16 * 8;
    private static final int IV_LENGTH = 12;

    private static final SecretKey SECRET_KEY;
    private static final boolean ENABLED;

    static {
        SecretKey key = null;
        boolean enabled = false;
        try {
            String b64 = System.getenv(ENV_KEY_NAME);
            if (b64 != null && !b64.isBlank()) {
                byte[] decoded = Base64.getDecoder().decode(b64);
                key = new SecretKeySpec(decoded, ALGO);
                enabled = true;
            } else {
                log.warn("Environment variable {} is not set - DB field encryption disabled.", ENV_KEY_NAME);
            }
        } catch (Exception e) {
            log.error("Failed to initialize encryption key from env {}: {}", ENV_KEY_NAME, e.getMessage(), e);
        }
        SECRET_KEY = key;
        ENABLED = enabled;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (!ENABLED || attribute == null) return attribute;

        try {
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, spec);
            byte[] cipherText = cipher.doFinal(attribute.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            log.error("Encryption failed, returning plain text. Error: {}", e.getMessage(), e);
            return attribute; // fallback to plain text to avoid breaking writes
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (!ENABLED || dbData == null) return dbData;

        try {
            byte[] bytes = Base64.getDecoder().decode(dbData);
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

            byte[] iv = new byte[IV_LENGTH];
            byteBuffer.get(iv);

            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, spec);
            byte[] plain = cipher.doFinal(cipherText);

            return new String(plain, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Decryption failed, returning DB value as-is. Error: {}", e.getMessage(), e);
            return dbData; // fallback
        }
    }
}