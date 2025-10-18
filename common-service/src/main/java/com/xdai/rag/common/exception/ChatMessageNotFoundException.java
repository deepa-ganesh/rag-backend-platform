package com.xdai.rag.common.exception;

public class ChatMessageNotFoundException extends RuntimeException {

    public ChatMessageNotFoundException(String message) {
        super(message);
    }
}
