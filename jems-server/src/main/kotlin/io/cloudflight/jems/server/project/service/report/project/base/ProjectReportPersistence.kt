package io.cloudflight.jems.server.project.service.report.project.base

import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportBudget
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

interface ProjectReportPersistence {

    fun listReports(projectId: Long, pageable: Pageable): Page<ProjectReportModel>

    fun getReportById(projectId: Long, reportId: Long): ProjectReportModel

    fun createReportAndFillItToEmptyCertificates(
        report: ProjectReportModel,
        targetGroups: List<ProjectRelevanceBenefit>,
        previouslyReportedByPartner: Map<Long, BigDecimal>,
        budget: ProjectReportBudget
    ): ProjectReportModel

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

    fun getSubmittedProjectReportIds(projectId: Long): Set<Long>

}
