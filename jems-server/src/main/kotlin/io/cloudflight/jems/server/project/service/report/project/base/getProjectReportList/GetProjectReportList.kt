package io.cloudflight.jems.server.project.service.report.project.base.getProjectReportList

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceSummaryModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportList(
    private val reportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
) : GetProjectReportListInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportListException::class)
    override fun findAll(projectId: Long, pageable: Pageable): Page<ProjectReportSummary> {
        val latestReport = reportPersistence.getCurrentLatestReportFor(projectId)
        val latestDeletableReportId = if (latestReport != null && latestReport.status.isOpen()) latestReport.id else null

        return reportPersistence.listReports(projectId, pageable)
            .map { it.toServiceSummaryModel(deletableId = latestDeletableReportId, it.periodResolver()) }
    }

    private fun ProjectReportModel.periodResolver(): (Int) -> ProjectPeriod? = { periodNumber ->
        projectPersistence.getProjectPeriods(projectId, linkedFormVersion)
            .firstOrNull { it.number == periodNumber }
    }

}
