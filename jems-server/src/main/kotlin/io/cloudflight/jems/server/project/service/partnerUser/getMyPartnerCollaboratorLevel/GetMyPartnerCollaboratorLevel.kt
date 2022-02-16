package io.cloudflight.jems.server.project.service.partnerUser.getMyPartnerCollaboratorLevel

import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.EDIT
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.VIEW
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectRetrieve
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectReportingEdit
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectReportingView
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyPartnerCollaboratorLevel(
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val securityService: SecurityService,
) : GetMyPartnerCollaboratorLevelInteractor {

    // no security needed
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetMyPartnerCollaboratorLevelException::class)
    override fun getMyPartnerCollaboratorLevel(partnerId: Long): PartnerCollaboratorLevel? {
        val highestFromPermissions = securityService.currentUser
            ?.getHighestReportingLevelFromPermission(projectId = partnerPersistence.getProjectIdForPartnerId(partnerId))
        val highestFromCollaborators = getHighestReportingLevelFromCollaborators(partnerId = partnerId)

        return when(true) {
            highestFromCollaborators == EDIT || highestFromPermissions == EDIT -> EDIT
            highestFromCollaborators == VIEW || highestFromPermissions == VIEW -> VIEW
            else -> null
        }
    }

    private fun CurrentUser.getHighestReportingLevelFromPermission(projectId: Long): PartnerCollaboratorLevel? =
        when (true) {
            this.hasPermission(ProjectRetrieve),
            this.hasPermission(ProjectReportingEdit) && user.assignedProjects.contains(projectId) ->
                ProjectReportingEdit.getCorrespondingCollaboratorLevel()

            this.hasPermission(ProjectReportingView) && user.assignedProjects.contains(projectId) ->
                ProjectReportingView.getCorrespondingCollaboratorLevel()

            else -> null
        }

    private fun getHighestReportingLevelFromCollaborators(partnerId: Long): PartnerCollaboratorLevel? =
        partnerCollaboratorPersistence.findByUserIdAndPartnerId(
            userId = securityService.getUserIdOrThrow(),
            partnerId = partnerId,
        ).orElse(null)

    private fun UserRolePermission.getCorrespondingCollaboratorLevel(): PartnerCollaboratorLevel? =
        when (this) {
            ProjectReportingEdit -> EDIT
            ProjectReportingView -> VIEW
            else -> null
        }

}
