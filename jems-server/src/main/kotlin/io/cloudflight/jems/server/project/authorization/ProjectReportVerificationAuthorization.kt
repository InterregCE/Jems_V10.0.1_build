package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canViewProjectReportVerificationByReportId(#projectReportId)")
annotation class CanViewProjectReportVerificationByReportId

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canEditProjectReportVerificationByReportId(#projectReportId)")
annotation class CanEditProjectReportVerificationByReportId

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canViewProjectReportVerification(#projectId)")
annotation class CanViewReportVerification

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canEditProjectReportVerification(#projectId)")
annotation class CanEditReportVerification

@Component
class ProjectReportVerificationAuthorization(
    override val securityService: SecurityService,
    private val projectPersistence: ProjectPersistence,
    private val projectReportPersistence: ProjectReportPersistence
) : Authorization(securityService) {

    fun canViewProjectReportVerificationByReportId(projectReportId: Long) =
        canViewProjectReportVerification(projectId = projectReportId.toProjectId())

    fun canEditProjectReportVerificationByReportId(projectReportId: Long) =
        canEditProjectReportVerification(projectId = projectReportId.toProjectId())

    fun canViewProjectReportVerification(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)

        val canMonitorView = hasPermission(UserRolePermission.ProjectReportingVerificationProjectView, projectId)
        val canCreatorView = isActiveUserIdEqualToOneOf(project.getUserIdsWithViewLevel())

        return canMonitorView || canCreatorView
    }

    fun canEditProjectReportVerification(projectId: Long): Boolean {
        val project = projectPersistence.getApplicantAndStatusById(projectId)

        val canMonitorEdit = hasPermission(UserRolePermission.ProjectReportingVerificationProjectEdit, projectId)
        val canCreatorEdit = isActiveUserIdEqualToOneOf(project.getUserIdsWithEditLevel())

        return canMonitorEdit || canCreatorEdit
    }

    private fun Long.toProjectId() = projectReportPersistence.getProjectIdForProjectReportId(this)

}
