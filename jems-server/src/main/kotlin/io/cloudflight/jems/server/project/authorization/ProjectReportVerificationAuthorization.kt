package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

// expenditure tab:
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.hasPermissionForProjectReportId('ProjectReportingVerificationProjectView', #reportId)")
annotation class CanViewReportVerificationExpenditure

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.hasPermissionForProjectReportId('ProjectReportingVerificationProjectEdit', #reportId)")
annotation class CanEditReportVerificationExpenditure

// document tab:
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canInteractWithVerificationDocuments(#projectId, false)")
annotation class CanViewReportVerificationCommunication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canInteractWithVerificationDocuments(#projectId, true)")
annotation class CanEditReportVerificationCommunication

// financial overview tab:
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canViewReportVerificationFinance(#reportId)")
annotation class CanViewReportVerificationFinance

// finalize and checklist tabs:
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.hasPermissionForProjectReportId('ProjectReportingVerificationFinalize', #reportId)")
annotation class CanFinalizeReportVerification

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectReportingVerificationProjectView', #projectId)")
annotation class CanViewReportVerificationPrivileged

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectReportingVerificationProjectEdit', #projectId)")
annotation class CanEditReportVerificationPrivileged

@Component
class ProjectReportVerificationAuthorization(
    private val projectReportPersistence: ProjectReportPersistence,
    private val userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence,
    override val securityService: SecurityService,
) : Authorization(securityService) {


    fun canViewReportVerificationFinance(reportId: Long): Boolean {
        val report = projectReportPersistence.getReportByIdUnSecured(reportId)
        val collaboratorIds = userProjectCollaboratorPersistence.getUserIdsForProject(report.projectId)
            .mapTo(HashSet()) { it.userId }

        // creator can = only project manager when verification has been already done
        val canCreatorView = isActiveUserIdEqualToOneOf(collaboratorIds) && report.status.isFinalized()
        val canMonitorView = hasPermission(UserRolePermission.ProjectReportingVerificationProjectView, report.projectId)

        return canCreatorView || canMonitorView
    }

    fun hasPermissionForProjectReportId(permission: UserRolePermission, reportId: Long) =
        hasPermissionForProject(permission, reportId.toProjectId())

    fun canInteractWithVerificationDocuments(projectId: Long, isEditRequired: Boolean): Boolean {
        val collaboratorIds = userProjectCollaboratorPersistence.getUserIdsForProject(projectId)
            .mapTo(HashSet()) { it.userId }

        val canCreator = isActiveUserIdEqualToOneOf(collaboratorIds)
        val canMonitor = hasPermission(
            if (isEditRequired)
                UserRolePermission.ProjectReportingVerificationProjectEdit
            else
                UserRolePermission.ProjectReportingVerificationProjectView,
            projectId,
        )

        return canCreator || canMonitor
    }

    private fun Long.toProjectId() = projectReportPersistence.getReportByIdUnSecured(this).projectId

}
