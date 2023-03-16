package io.cloudflight.jems.server.notification.repository

import io.cloudflight.jems.server.notification.entity.NotificationEntity
import io.cloudflight.jems.server.notification.model.Notification
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.ZonedDateTime

fun List<Notification>.toEntities(
    project: ProjectEntity,
    users: Map<String, UserEntity>
) = map {
    NotificationEntity(
        userEntity = users[it.email]!!,
        created = ZonedDateTime.now(),
        project = project,
        subject = it.subject,
        body = it.body,
        type = it.type
    )
}
