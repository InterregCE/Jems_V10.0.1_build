package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canEditReport(#projectId, #reportId)")
annotation class CanEditProjectReport

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canEditReportNotSpecific(#projectId)")
annotation class CanEditProjectReportNotSpecific

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canViewReport(#projectId)")
annotation class CanRetrieveProjectReport

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canStartReportVerification(#projectId, #reportId)")
annotation class CanStartProjectReportVerification

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canViewReportVerification(#projectId)")
annotation class CanViewReportVerification
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canEditReportVerification(#projectId)")
annotation class CanEditReportVerification

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canFinalizeReportVerification(#projectId)")
annotation class CanFinalizeProjectReportVerification

@Component
class ProjectReportAuthorization(
    override val securityService: SecurityService,
    private val reportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
) : Authorization(securityService) {

    fun canEditReport(projectId: Long, reportId: Long): Boolean {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId)
        return report.status.isOpen() && canEditReportNotSpecific(projectId)
    }

    fun canEditReportNotSpecific(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)

        val canMonitorEdit = hasPermission(UserRolePermission.ProjectReportingProjectEdit, projectId)
        val canCreatorEdit = isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel())

        return canMonitorEdit || canCreatorEdit
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

    fun canFinalizeReportVerification(projectId: Long): Boolean =
        hasPermission(UserRolePermission.ProjectReportingVerificationFinalize, projectId)

    fun canEditReportVerification(projectId: Long): Boolean =
        hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit, projectId)

    fun canViewReportVerification(projectId: Long): Boolean =
        hasPermission(UserRolePermission.ProjectReportingVerificationProjectView, projectId)

}
