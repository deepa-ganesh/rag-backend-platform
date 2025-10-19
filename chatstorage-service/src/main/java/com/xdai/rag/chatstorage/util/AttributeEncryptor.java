package com.xdai.rag.chatstorage.util;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

@Converter
@Component
@Slf4j
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static final String ALGO = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16 * 8;
    private static final int IV_LENGTH = 12;

    @Value("${app.encryption.key:}")
    private String encryptionKeyProperty;

    private static SecretKey SECRET_KEY;
    private static boolean ENABLED;

    @PostConstruct
    private void init() {
        try {
            if (encryptionKeyProperty != null && !encryptionKeyProperty.isBlank()) {
                byte[] decoded = Base64.getDecoder().decode(encryptionKeyProperty);
                SECRET_KEY = new SecretKeySpec(decoded, ALGO);
                ENABLED = true;
                log.info("Database field encryption enabled.");
            } else {
                ENABLED = false;
                log.warn("No encryption key configured — database field encryption disabled.");
            }
        } catch (Exception e) {
            ENABLED = false;
            log.error("Failed to initialize encryption key: {}", e.getMessage(), e);
        }
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
            log.error("Encryption failed, returning plain text: {}", e.getMessage(), e);
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
            log.error("Decryption failed, returning DB value as-is: {}", e.getMessage(), e);
            return dbData;
        }
    }
}
