package io.cloudflight.jems.server.notification.repository

import io.cloudflight.jems.server.notification.entity.NotificationEntity
import io.cloudflight.jems.server.notification.model.Notification
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.ZonedDateTime

fun List<Notification>.toEntities(
    creationTime: ZonedDateTime,
    users: Map<String, UserEntity>
) = map {
    NotificationEntity(
        userEntity = users[it.email]!!,
        created = creationTime,
        projectId = it.project?.projectId,
        projectIdentifier = it.project?.projectIdentifier,
        projectAcronym = it.project?.projectAcronym,
        subject = it.subject,
        body = it.body,
        type = it.type
    )
}
