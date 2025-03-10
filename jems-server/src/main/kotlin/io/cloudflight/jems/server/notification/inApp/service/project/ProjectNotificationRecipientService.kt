package io.cloudflight.jems.server.notification.inApp.service.project

import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectPersistence
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.UserEmailNotification
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.assignment.CollaboratorAssignedToProject
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProjectNotificationRecipientService(
    private val userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence,
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val userProjectPersistence: UserProjectPersistence,
    private val userPersistence: UserPersistence,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
) : ProjectNotificationRecipientServiceInteractor {

    @Transactional(readOnly = true)
    override fun getEmailsForProjectManagersAndAssignedUsers(
        notificationConfig: ProjectNotificationConfiguration,
        projectId: Long
    ): Map<String, UserEmailNotification> {
        val managers = if (!notificationConfig.sendToManager) emptyMap() else
            userProjectCollaboratorPersistence.getCollaboratorsForProject(projectId).emails()

        val projectAssignedUsers = if (!notificationConfig.sendToProjectAssigned) emptyMap() else
            userProjectPersistence.getUsersForProject(projectId)
                .emails()

        return managers + projectAssignedUsers
    }

    @Transactional(readOnly = true)
    override fun getEmailsForPartners(
        notificationConfig: ProjectNotificationConfiguration,
        projectId: Long
    ): Map<String, UserEmailNotification> {
        val partnerIdsByType = if (!notificationConfig.sendToLeadPartner && !notificationConfig.sendToProjectPartners) emptyMap() else
            partnerPersistence.findTop50ByProjectId(projectId).groupBy({ it.role }, { it.id })
        val leadPartnerId = partnerIdsByType[ProjectPartnerRole.LEAD_PARTNER]?.firstOrNull()
        val partnerIds = partnerIdsByType[ProjectPartnerRole.PARTNER]?.toSet() ?: emptySet()

        val leadPartnerCollaborators = if (leadPartnerId == null || !notificationConfig.sendToLeadPartner) emptyMap() else
            partnerCollaboratorPersistence.findByProjectAndPartners(projectId, setOf(leadPartnerId))
                .partnerCollaboratorEmails()

        val nonLeadPartnerCollaborators = if (partnerIds.isEmpty() || !notificationConfig.sendToProjectPartners) emptyMap() else
            partnerCollaboratorPersistence.findByProjectAndPartners(projectId, partnerIds)
                .partnerCollaboratorEmails()

        return leadPartnerCollaborators + nonLeadPartnerCollaborators
    }

    @Transactional(readOnly = true)
    override fun getEmailsForSpecificPartner(
        notificationConfig: ProjectNotificationConfiguration,
        projectId: Long,
        partnerId: Long,
    ): Map<String, UserEmailNotification> {
        if (!notificationConfig.sendToLeadPartner && !notificationConfig.sendToProjectPartners)
            return emptyMap()
        val partnerDetails = partnerPersistence.getById(partnerId)
        val sendToPartner = when(partnerDetails.role) {
            ProjectPartnerRole.LEAD_PARTNER -> notificationConfig.sendToLeadPartner
            ProjectPartnerRole.PARTNER -> notificationConfig.sendToProjectPartners
        }
        return if (!sendToPartner) emptyMap() else
            partnerCollaboratorPersistence.findByProjectAndPartners(projectId, setOf(partnerId))
                .partnerCollaboratorEmails()
    }

    @Transactional(readOnly = true)
    override fun getEmailsForPartnerControllers(
        notificationConfig: ProjectNotificationConfiguration,
        partnerId: Long
    ): Map<String, UserEmailNotification> {
        return if (!notificationConfig.sendToControllers) emptyMap() else
            userPersistence.findAllByIds(controllerInstitutionPersistence.getRelatedUserIdsForPartner(partnerId)).emails()
    }

    @Transactional(readOnly = true)
    override fun getSystemAdminEmails(): Map<String, UserEmailNotification> =
        userPersistence.findAllWithRoleIdIn(setOf(1L)).emails()

    private fun Set<PartnerCollaborator>.partnerCollaboratorEmails() =
        associateBy({ it.userEmail }, { UserEmailNotification(it.sendNotificationsToEmail, it.userStatus) })

    private fun Collection<UserSummary>.emails() =
        associateBy({ it.email }, { UserEmailNotification(it.sendNotificationsToEmail, it.userStatus) })

    private fun List<CollaboratorAssignedToProject>.emails() =
        associateBy({ it.userEmail }, { UserEmailNotification(it.sendNotificationsToEmail, it.userStatus) })

}
