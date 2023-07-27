package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canViewProjectReportVerification(#projectReportId)")
annotation class CanViewProjectReportVerificationByReportId

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportVerificationAuthorization.canEditProjectReportVerification(#projectReportId)")
annotation class CanEditProjectReportVerificationByReportId

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canViewReportVerification(#projectId)")
annotation class CanViewReportVerification
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@projectReportAuthorization.canEditReportVerification(#projectId)")
annotation class CanEditReportVerification

@Component
class ProjectReportVerificationAuthorization(
    override val securityService: SecurityService,
    private val projectReportPersistence: ProjectReportPersistence
) : Authorization(securityService) {

    fun canViewProjectReportVerification(projectReportId: Long): Boolean =
        hasPermission(
            UserRolePermission.ProjectReportingVerificationProjectView,
            projectId = projectReportId.toProjectId(),
        )

    fun canEditProjectReportVerification(projectReportId: Long): Boolean =
        hasPermission(
            UserRolePermission.ProjectReportingVerificationProjectView,
            projectId = projectReportId.toProjectId(),
        )

    private fun Long.toProjectId() = projectReportPersistence.getProjectIdForProjectReportId(this)

}
