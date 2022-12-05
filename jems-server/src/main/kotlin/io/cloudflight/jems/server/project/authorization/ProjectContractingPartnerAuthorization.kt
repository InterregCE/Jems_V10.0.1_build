package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import java.util.Optional

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingPartnerAuthorization.hasViewPermission(#partnerId)")
annotation class CanRetrieveProjectContractingPartner

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectContractingPartnerAuthorization.hasEditPermission(#partnerId)")
annotation class CanUpdateProjectContractingPartner

@Component
class ProjectContractingPartnerAuthorization(
    override val securityService: SecurityService,
    val partnerPersistence: PartnerPersistence,
    val projectPersistence: ProjectPersistence,
    val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val projectCollaboratorPersistence: UserProjectCollaboratorPersistence,
    private val projectReportAuthorization: ProjectReportAuthorization,
    val projectAuthorization: ProjectAuthorization,
): Authorization(securityService) {

    fun hasViewPermission(partnerId: Long): Boolean {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)

        // monitor users
        if (projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerView, projectId))
            return true

        // partner collaborators and controller institutions
        val levelForCollaborator = projectReportAuthorization.getLevelForUserCollaborator(partnerId = partnerId)
        val levelForController = projectReportAuthorization.getLevelForUserController(partnerId = partnerId)
        // extra check global project collaborators
        val levelForCollaboratorProject = getLevelForUserCollaboratorProject(projectId = projectId)

        return levelForCollaborator.isPresent || levelForController.isPresent || levelForCollaboratorProject.isPresent
    }

    fun hasEditPermission(partnerId: Long): Boolean {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        val project = projectPersistence.getApplicantAndStatusById(projectId)
        val hasProjectPermission = projectAuthorization.hasPermission(UserRolePermission.ProjectContractingPartnerEdit, projectId)
        val isProjectCollaboratorWithEdit = isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel())
        val level = partnerCollaboratorPersistence.findByUserIdAndPartnerId(
            userId = securityService.getUserIdOrThrow(),
            partnerId = partnerId,
        )
        return (level.isPresent && level.get() == PartnerCollaboratorLevel.EDIT) || hasProjectPermission || isProjectCollaboratorWithEdit
    }

    fun getLevelForUserCollaboratorProject(projectId: Long) =
        Optional.ofNullable(
            projectCollaboratorPersistence.getLevelForProjectAndUser(
                projectId = projectId,
                userId = securityService.getUserIdOrThrow(),
            )
        )

}
