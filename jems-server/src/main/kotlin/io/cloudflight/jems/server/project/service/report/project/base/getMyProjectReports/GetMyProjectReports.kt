package io.cloudflight.jems.server.project.service.report.project.base.getMyProjectReports

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewMyProjectReports
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceSummaryModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetMyProjectReports(
    private val reportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
    private val projectCollaboratorPersistence: UserProjectCollaboratorPersistence,
    private val securityService: SecurityService
) : GetMyProjectReportsInteractor {

    @CanViewMyProjectReports
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetMyProjectReportsException::class)
    override fun findAllOfMine(pageable: Pageable): Page<ProjectReportSummary> {
        val projectIds = getAllProjectIdsRelatedToCurrentUser()
        return reportPersistence.listProjectReports(projectIds, ProjectReportStatus.SUBMITTED_STATUSES, pageable)
            .map { it.toServiceSummaryModel(it.periodResolver()) }
    }

    private fun getAllProjectIdsRelatedToCurrentUser(): Set<Long> {
        val assignedProjectIds = securityService.currentUser?.user?.assignedProjects ?: emptySet()
        val projectCollaboratorProjectIds = projectCollaboratorPersistence.getProjectIdsForUser(securityService.getUserIdOrThrow())
        return assignedProjectIds union projectCollaboratorProjectIds
    }

    private fun ProjectReportModel.periodResolver(): (Int) -> ProjectPeriod? = { periodNumber ->
        projectPersistence.getProjectPeriods(projectId, linkedFormVersion)
            .firstOrNull { it.number == periodNumber }
    }
}
