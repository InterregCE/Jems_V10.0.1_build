package io.cloudflight.jems.server.notification.inApp.repository

import io.cloudflight.jems.server.notification.inApp.entity.NotificationEntity
import io.cloudflight.jems.server.notification.inApp.service.model.Notification
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProject
import io.cloudflight.jems.server.notification.inApp.service.model.UserNotification
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import org.springframework.data.domain.Page
import java.time.LocalDateTime
import java.time.ZoneOffset

fun Notification.toUsers(
    users: Iterable<UserEntity>,
    projectResolver: (Long) -> ProjectEntity,
) = users.map {
    NotificationEntity(
        account = it,
        created = LocalDateTime.ofInstant(time.toInstant(), ZoneOffset.UTC),
        project = project?.let { projectResolver.invoke(it.projectId) },
        projectIdentifier = project?.projectIdentifier,
        projectAcronym = project?.projectAcronym,
        subject = subject,
        body = body,
        type = type,
    )
}

fun Page<NotificationEntity>.toModel() = map { it.toModel() }

fun NotificationEntity.toModel() = UserNotification(
    id = id,
    project = project?.let {
        NotificationProject(
            callId = it.call.id,
            callName = it.call.name,
            projectId = it.id,
            projectIdentifier = projectIdentifier!!,
            projectAcronym = projectAcronym!!,
        )
    },
    time = created.atOffset(ZoneOffset.UTC).toZonedDateTime(),
    subject = subject,
    body = body,
    type = type,
)
