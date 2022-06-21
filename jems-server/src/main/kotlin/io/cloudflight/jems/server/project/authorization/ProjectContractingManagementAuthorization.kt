package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component


@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingManagementAuthorization.canViewProjectManagement(#projectId)")
annotation class CanViewProjectManagement

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingManagementAuthorization.canEditProjectManagement(#projectId)")
annotation class CanEditProjectManagement

@Component
class ProjectContractingManagementAuthorization(
    override val securityService: SecurityService,
    val partnerPersistence: PartnerPersistence,
    val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    val projectPersistence: ProjectPersistence
): Authorization(securityService) {

    fun canViewProjectManagement(projectId: Long): Boolean {
        val currentUserId = securityService.getUserIdOrThrow()
        val applicantAndStatus = projectPersistence.getApplicantAndStatusById(projectId)

        return hasPermissionForProject(UserRolePermission.ProjectContractingManagementView, projectId) ||
            hasPermissionForProject(UserRolePermission.ProjectContractingManagementEdit, projectId) ||
            userIsProjectOwnerOrProjectCollaborator(userId = currentUserId, applicantAndStatus = applicantAndStatus) ||
            userIsPartnerCollaborator(userId = currentUserId, projectId = projectId, )
    }

    fun canEditProjectManagement(projectId: Long): Boolean {
        val currentUserId = securityService.getUserIdOrThrow()
        val applicantAndStatus = projectPersistence.getApplicantAndStatusById(projectId)
        return hasPermissionForProject(UserRolePermission.ProjectContractingManagementEdit, projectId) ||
            applicantAndStatus.applicantId == currentUserId ||
            userIsProjectCollaboratorWithEditPrivilege(currentUserId, applicantAndStatus)
    }


    private fun userIsProjectOwnerOrProjectCollaborator(userId: Long, applicantAndStatus: ProjectApplicantAndStatus): Boolean {
        return applicantAndStatus.applicantId == userId || userId in applicantAndStatus.getUserIdsWithViewLevel()
    }

    private fun userIsProjectCollaboratorWithEditPrivilege(userId: Long, applicantAndStatus: ProjectApplicantAndStatus): Boolean {
        return userId in applicantAndStatus.getUserIdsWithEditLevel()
    }

    private fun userIsPartnerCollaborator(userId: Long, projectId: Long): Boolean =
        getUserPartnerCollaborations(userId = userId, projectId = projectId).isNotEmpty()

    private fun getUserPartnerCollaborations(userId: Long, projectId: Long) =
        partnerCollaboratorPersistence.findPartnersByUserAndProject(
            userId = userId,
            projectId = projectId,
        )


}
