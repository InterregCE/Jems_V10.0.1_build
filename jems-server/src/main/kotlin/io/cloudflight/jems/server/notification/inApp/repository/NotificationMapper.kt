package io.cloudflight.jems.server.notification.inApp.repository

import io.cloudflight.jems.server.notification.inApp.entity.NotificationEntity
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationInApp
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProject
import io.cloudflight.jems.server.notification.inApp.service.model.UserNotification
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import org.springframework.data.domain.Page
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

fun NotificationInApp.toUsers(
    groupId: UUID,
    recipientsResolver: (Set<String>) -> Iterable<UserEntity>,
    projectResolver: (Long) -> ProjectEntity,
) = recipientsResolver.invoke(recipientsInApp).map {
    NotificationEntity(
        account = it,
        groupIdentifier = groupId,
        instanceIdentifier = UUID.randomUUID(),
        created = LocalDateTime.ofInstant(time.toInstant(), ZoneOffset.UTC),
        project = templateVariables.getProjectId()?.let { projectResolver.invoke(it) },
        projectIdentifier = templateVariables.getProjectIdentifier(),
        projectAcronym = templateVariables.getProjectAcronym(),
        subject = subject,
        body = body,
        type = type,
    )
}

private fun Map<String, Any>.getProjectId(): Long? =
    get("projectId").let { if (it is Long) it else null }
private fun Map<String, Any>.getProjectIdentifier(): String? =
    get("projectIdentifier").let { if (it is String) it else null }
private fun Map<String, Any>.getProjectAcronym(): String? =
    get("projectAcronym").let { if (it is String) it else null }

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
