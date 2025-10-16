package com.xdai.ragchatstorage.mapper;

import com.xdai.ragchatstorage.dto.ChatSessionRequest;
import com.xdai.ragchatstorage.dto.ChatSessionResponse;
import com.xdai.ragchatstorage.entity.ChatSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatSessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "favorite", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ChatSession toEntity(ChatSessionRequest request);

    ChatSessionResponse toResponse(ChatSession entity);
}
