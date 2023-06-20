package io.cloudflight.jems.server.project.service.report.project.base

import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportBaseData
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectReportPersistence {

    fun listReports(projectId: Long, pageable: Pageable): Page<ProjectReportModel>

    fun getAllProjectReportsBaseDataByProjectId(projectId: Long): Sequence<ProjectReportBaseData>

    fun getReportById(projectId: Long, reportId: Long): ProjectReportModel

    fun updateReport(
        projectId: Long,
        reportId: Long,
        startDate: LocalDate?,
        endDate: LocalDate?,
        deadline: ProjectReportDeadline,
    ): ProjectReportModel

    fun getCurrentSpendingProfile(reportId: Long): Map<Long, BigDecimal>

    fun updateSpendingProfile(reportId: Long, currentValuesByPartnerId: Map<Long, BigDecimal>)

    fun deleteReport(projectId: Long, reportId: Long)

    fun getCurrentLatestReportFor(projectId: Long): ProjectReportModel?

    fun countForProject(projectId: Long): Int

    fun submitReport(projectId: Long, reportId: Long, submissionTime: ZonedDateTime): ProjectReportSubmissionSummary

    fun getSubmittedProjectReportIds(projectId: Long): List<Pair<Long, ContractingDeadlineType>>

    fun getDeadlinesWithLinkedReportStatus(projectId: Long): Map<Long, ProjectReportStatus>

    fun decreaseNewerReportNumbersIfAllOpen(projectId: Long, number: Int)

    fun exists(projectId: Long, reportId: Long): Boolean

    fun startVerificationOnReportById(projectId: Long, reportId: Long): ProjectReportSubmissionSummary
}
