package com.xdai.rag.chatstorage.exception;

public class ChatSessionNotFoundException extends RuntimeException {

    public ChatSessionNotFoundException(String message) {
        super(message);
    }
}
