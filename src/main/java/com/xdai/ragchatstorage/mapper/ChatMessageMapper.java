package com.xdai.ragchatstorage.mapper;

import com.xdai.ragchatstorage.dto.ChatMessageRequest;
import com.xdai.ragchatstorage.dto.ChatMessageResponse;
import com.xdai.ragchatstorage.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chatSession", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ChatMessage toEntity(ChatMessageRequest request);

    ChatMessageResponse toResponse(ChatMessage entity);
}
