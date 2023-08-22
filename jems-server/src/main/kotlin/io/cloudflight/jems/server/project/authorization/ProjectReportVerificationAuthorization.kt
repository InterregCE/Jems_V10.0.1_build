package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
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
@PreAuthorize("@projectReportVerificationAuthorization.canViewDocuments(#projectId)")
annotation class CanViewReportVerificationCommunication

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canEditDocuments(#projectId)")
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
    override val securityService: SecurityService,
    private val userProjectCollaboratorPersistence: UserProjectCollaboratorPersistence,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
    private val partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence,
    private val projectPersistence: ProjectPersistence
) : Authorization(securityService) {

    fun canViewReportVerificationFinance(reportId: Long): Boolean {
        val report = projectReportPersistence.getReportByIdUnSecured(reportId)
        val collaboratorIds = userProjectCollaboratorPersistence.getUserIdsForProject(report.projectId)
            .mapTo(HashSet()) { it.userId } union partnerCollaboratorPersistence.findUserIdsByProjectId(report.projectId)
        val controllers = controllerInstitutionPersistence.getRelatedUserIdsForProject(report.projectId)

        // creator can = only collaborators when verification has been already done
        val canApplicantView = isActiveUserIdEqualToOneOf(collaboratorIds) && report.status.isFinalized()
        val canMonitorView = hasPermission(UserRolePermission.ProjectReportingVerificationProjectView, report.projectId)
        val canControllerView = isActiveUserIdEqualToOneOf(controllers)
            && hasNonProjectAuthority(UserRolePermission.ProjectReportingVerificationProjectView)

        return canApplicantView || canMonitorView || canControllerView
    }

    fun hasPermissionForProjectReportId(permission: UserRolePermission, reportId: Long) =
        hasPermissionForProject(permission, reportId.toProjectId())

    fun canEditDocuments(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)

        val canMonitorEdit = hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit, projectId)
        val canCreatorEdit = isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel())

        return canMonitorEdit || canCreatorEdit
    }

    fun canViewDocuments(projectId: Long): Boolean {
        val isProjectCollaborator = userProjectCollaboratorPersistence
            .getLevelForProjectAndUser(projectId, securityService.getUserIdOrThrow()) != null
        val isPartnerCollaborator = isActiveUserIdEqualToOneOf(partnerCollaboratorPersistence.findUserIdsByProjectId(projectId))
        val isMonitorWithView = hasPermission(UserRolePermission.ProjectReportingVerificationProjectView, projectId)
        val isControllerWithView = isActiveUserIdEqualToOneOf(controllerInstitutionPersistence.getRelatedUserIdsForProject(projectId))
            && hasNonProjectAuthority(UserRolePermission.ProjectReportingVerificationProjectView)

        return isProjectCollaborator || isPartnerCollaborator || isMonitorWithView || isControllerWithView
    }


    private fun Long.toProjectId() = projectReportPersistence.getReportByIdUnSecured(this).projectId

}
