package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.assignment.PartnerCollaboratorData
import io.cloudflight.jems.plugin.contract.models.project.assignment.ProjectCollaboratorLevelData
import io.cloudflight.jems.plugin.contract.models.user.CurrentUserData
import io.cloudflight.jems.plugin.contract.services.privileges.CurrentUserDataProvider
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.CurrentUserIdIsNullException
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.stereotype.Service

@Service
class CurrentUserDataProviderImpl(
    val securityService: SecurityService,
    val authorization: Authorization,
    val userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence,
    val userPartnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    val partnerPersistence: PartnerPersistence,
) : CurrentUserDataProvider {

    override fun getCurrentUserData(): CurrentUserData =
        securityService.currentUser?.toDataModel() ?: throw CurrentUserIdIsNullException()

    override fun hasPermission(rolePermission: String, projectId: Long?): Boolean =
        authorization.hasPermission(UserRolePermission.valueOf(rolePermission), projectId)

    override fun getPartnerCollaboratorLevel(partnerId: Long): PartnerCollaboratorData? {
        val currentUserId = securityService.getUserIdOrThrow()
        val level = userPartnerCollaboratorPersistence.findByUserIdAndPartnerId(partnerId = partnerId, userId = currentUserId)
        val gdpr = userPartnerCollaboratorPersistence.canUserSeePartnerSensitiveData(partnerId = partnerId, userId = currentUserId)

        return level.map {
            PartnerCollaboratorData(
                partnerId = partnerId,
                userId = currentUserId,
                userEmail = securityService.currentUser!!.user.email,
                level = it.toDataModel(),
                gdpr = gdpr
            )
        }
            .orElse(null)
    }

    override fun getPartnerCollaboratorsPerPartnerLevel(projectId: Long): Map<Long, PartnerCollaboratorData> =
        userPartnerCollaboratorPersistence.findPartnersByUserAndProject(projectId = projectId, userId = securityService.getUserIdOrThrow())
            .associate { it.partnerId to it.toDataModel() }

    override fun getProjectCollaboratorLevel(projectId: Long): ProjectCollaboratorLevelData? =
        userProjectCollaboratorPersistence.getLevelForProjectAndUser(projectId = projectId, userId = securityService.getUserIdOrThrow())?.toDataModel()

}
