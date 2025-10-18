package com.xdai.rag.chatstorage.mapper;

import com.xdai.rag.chatstorage.dto.ChatMessageRequest;
import com.xdai.rag.chatstorage.dto.ChatMessageResponse;
import com.xdai.rag.chatstorage.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "session", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ChatMessage toEntity(ChatMessageRequest request);

    ChatMessageResponse toResponse(ChatMessage entity);

    List<ChatMessageResponse> toResponseList(List<ChatMessage> entities);
}
