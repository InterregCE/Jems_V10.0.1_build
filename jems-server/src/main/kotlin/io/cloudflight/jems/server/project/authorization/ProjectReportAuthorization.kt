package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canEditReport(#reportId)")
annotation class CanEditProjectReport

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canCreateProjectReport(#projectId)")
annotation class  CanCreateProjectReport

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectAuthorization.hasPermission('ProjectReportsRetrieve')")
annotation class CanViewMyProjectReports

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canViewReport(#projectId)")
annotation class CanRetrieveProjectReport

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canStartReportVerification(#projectId)")
annotation class CanStartProjectReportVerification

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canReOpenProjectReport(#projectId)")
annotation class CanReOpenProjectReport

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canReOpenVerificationProjectReport(#projectId)")
annotation class CanReOpenVerificationProjectReport

@Component
class ProjectReportAuthorization(
    override val securityService: SecurityService,
    private val reportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
) : Authorization(securityService) {

    fun canEditReport(reportId: Long): Boolean {
        val report = reportPersistence.getReportByIdUnSecured(reportId = reportId)
        return !report.status.isClosed() && canEditReportNotSpecific(report.projectId)
    }

    fun canEditReportNotSpecific(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)

        val canMonitorEdit = hasPermission(UserRolePermission.ProjectReportingProjectEdit, projectId)
        val canCreatorEdit = isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel())

        return canMonitorEdit || canCreatorEdit
    }

    fun canCreateProjectReport(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)

        val canMonitorEdit = hasPermission(UserRolePermission.ProjectReportingProjectEdit, projectId)
        val canCreatorEdit = isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel())

        return canMonitorEdit ||
                (canCreatorEdit && hasPermission(UserRolePermission.ProjectCreatorReportingProjectCreate))
    }

    fun canViewReport(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)

        val canMonitorView = hasPermission(UserRolePermission.ProjectReportingProjectView, projectId)
        val canCreatorView = isActiveUserIdEqualToOneOf(project.getUserIdsWithViewLevel())

        return canMonitorView || canCreatorView
    }

    fun canStartReportVerification(projectId: Long): Boolean {
        return hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit, projectId)
    }

    fun canReOpenProjectReport(projectId: Long): Boolean {
        val permission = UserRolePermission.ProjectReportingProjectReOpen

        return hasPermissionForProject(permission, projectId = projectId) // assigned programme user with reopen
                || hasControllerPermission(permission, projectId = projectId) // controller with reopen
    }

    fun canReOpenVerificationProjectReport(projectId: Long): Boolean {
        return hasPermissionForProject(UserRolePermission.ProjectReportingVerificationReOpen, projectId = projectId)
            || hasControllerPermission(UserRolePermission.ProjectReportingVerificationReOpen, projectId = projectId)
    }

    private fun hasControllerPermission(permission: UserRolePermission, projectId: Long) =
        isActiveUserIdEqualToOneOf(controllerInstitutionPersistence.getRelatedUserIdsForProject(projectId))
                && hasNonProjectAuthority(permission)

}
