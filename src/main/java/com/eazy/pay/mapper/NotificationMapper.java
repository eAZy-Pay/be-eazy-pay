package com.eazy.pay.mapper;

import com.eazy.pay.dto.NotificationDTO;
import com.eazy.pay.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationMapper {

    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "userId", source = "user.uid")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "activeRead", source = "activeRead")
    NotificationDTO toDTO(Notification notification);

    @Mapping(target = "uid", source = "uid")
    @Mapping(target = "user.uid", source = "userId")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "activeRead", source = "activeRead")
    Notification toEntity(NotificationDTO notificationDTO);

}
