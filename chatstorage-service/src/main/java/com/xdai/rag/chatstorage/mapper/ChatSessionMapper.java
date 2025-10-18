package com.xdai.rag.chatstorage.mapper;

import com.xdai.rag.chatstorage.dto.ChatSessionRequest;
import com.xdai.rag.chatstorage.dto.ChatSessionResponse;
import com.xdai.rag.chatstorage.entity.ChatSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatSessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "favorite", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ChatSession toEntity(ChatSessionRequest request);

    ChatSessionResponse toResponse(ChatSession entity);

    List<ChatSessionResponse> toResponseList(List<ChatSession> entities);
}
