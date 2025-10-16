package com.xdai.ragchatstorage.exception;

public class ChatMessageNotFoundException extends RuntimeException {

    public ChatMessageNotFoundException(String message) {
        super(message);
    }
}
