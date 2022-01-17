package io.cloudflight.jems.server.notification.mail.repository

import io.cloudflight.jems.server.notification.mail.entity.MailNotificationEntity
import io.cloudflight.jems.server.notification.mail.service.model.MailNotification
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(MailNotificationMapper::class.java)

fun MailNotification.toEntity() =
    mapper.map(this)

fun MailNotificationEntity.toModel() =
    mapper.map(this)

@Mapper
abstract class MailNotificationMapper {
    abstract fun map(event: MailNotification): MailNotificationEntity
    abstract fun map(entity: MailNotificationEntity): MailNotification
}
