package io.cloudflight.jems.server.notification.inApp.repository

import io.cloudflight.jems.server.notification.inApp.entity.NotificationEntity
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationInApp
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationPartner
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationProject
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationVariable
import io.cloudflight.jems.server.notification.inApp.service.model.UserNotification
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import org.springframework.data.domain.Page
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

fun NotificationInApp.toUsers(
    recipients: Iterable<UserEntity>,
    projectResolver: (Long) -> ProjectEntity,
) = recipients.map {
    NotificationEntity(
        account = it,
        groupIdentifier = groupId,
        instanceIdentifier = UUID.randomUUID(),
        created = LocalDateTime.ofInstant(time.toInstant(), ZoneOffset.UTC),
        project = templateVariables.projectId()?.let { projectResolver.invoke(it) },
        projectIdentifier = templateVariables.projectIdentifier(),
        projectAcronym = templateVariables.projectAcronym(),
        partnerId = templateVariables.partnerId(),
        partnerRole = templateVariables.partnerRole(),
        partnerNumber = templateVariables.partnerNumber(),
        subject = subject,
        body = body,
        type = type,
    )
}

fun NotificationInApp.toUsersNonProject(
    recipients: Iterable<UserEntity>,
) = recipients.map {
    NotificationEntity(
        account = it,
        groupIdentifier = groupId,
        instanceIdentifier = UUID.randomUUID(),
        created = LocalDateTime.ofInstant(time.toInstant(), ZoneOffset.UTC),
        project = null,
        projectIdentifier = null,
        projectAcronym = null,
        partnerId = null,
        partnerRole = null,
        partnerNumber = null,
        subject = subject,
        body = body,
        type = type,
    )
}

fun Map<String, Any>.projectId() = get(NotificationVariable.ProjectId.variable) as? Long
fun Map<String, Any>.projectIdentifier() = get(NotificationVariable.ProjectIdentifier.variable) as? String
fun Map<String, Any>.projectAcronym() = get(NotificationVariable.ProjectAcronym.variable) as? String
fun Map<String, Any>.partnerId() = get(NotificationVariable.PartnerId.variable) as? Long
fun Map<String, Any>.partnerRole() = get(NotificationVariable.PartnerRole.variable) as? ProjectPartnerRole
fun Map<String, Any>.partnerNumber() = get(NotificationVariable.PartnerNumber.variable) as? Int

fun Page<NotificationEntity>.toModel() = map { it.toModel() }

fun NotificationEntity.toModel() = UserNotification(
    id = id,
    project = project?.let { getNotificationProject(it, projectIdentifier, projectAcronym) },
    partner = partnerId?.let { getNotificationPartner(it, partnerRole, partnerNumber) },
    time = created.atOffset(ZoneOffset.UTC).toZonedDateTime(),
    subject = subject,
    body = body,
    type = type,
)

fun getNotificationProject(project: ProjectEntity, projectIdentifier: String?, projectAcronym: String?) =
    NotificationProject(
        callId = project.call.id,
        callName = project.call.name,
        projectId = project.id,
        projectIdentifier = projectIdentifier!!,
        projectAcronym = projectAcronym!!,
    )

fun getNotificationPartner(partnerId: Long, partnerRole: ProjectPartnerRole?, partnerNumber: Int?) =
    NotificationPartner(
        partnerId = partnerId,
        partnerRole = partnerRole!!,
        partnerNumber = partnerNumber!!,
    )
