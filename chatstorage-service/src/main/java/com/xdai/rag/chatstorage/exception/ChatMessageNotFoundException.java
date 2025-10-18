package com.xdai.rag.chatstorage.exception;

public class ChatMessageNotFoundException extends RuntimeException {

    public ChatMessageNotFoundException(String message) {
        super(message);
    }
}
