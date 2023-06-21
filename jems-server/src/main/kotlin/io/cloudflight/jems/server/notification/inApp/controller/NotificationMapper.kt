package io.cloudflight.jems.server.notification.inApp.controller

import io.cloudflight.jems.api.notification.dto.NotificationDTO
import io.cloudflight.jems.server.notification.inApp.service.model.UserNotification
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

fun Page<UserNotification>.toModel() = map { mapper.map(it) }

private val mapper = Mappers.getMapper(NotificationMapper::class.java)

@Mapper
interface NotificationMapper {
    fun map(model: UserNotification): NotificationDTO
}
