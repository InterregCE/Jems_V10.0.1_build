package io.cloudflight.jems.server.project.service.report.project.base

import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface ProjectReportPersistence {

    fun listReports(projectId: Long, pageable: Pageable): Page<ProjectReportModel>

    fun getReportById(projectId: Long, reportId: Long): ProjectReportModel

    fun createReport(report: ProjectReportModel): ProjectReportModel

    fun updateReport(
        projectId: Long,
        reportId: Long,
        startDate: LocalDate?,
        endDate: LocalDate?,
        deadline: ProjectReportDeadline,
    ): ProjectReportModel

    fun deleteReport(projectId: Long, reportId: Long)

    fun getCurrentLatestReportFor(projectId: Long): ProjectReportModel?

    fun countForProject(projectId: Long): Int

}
