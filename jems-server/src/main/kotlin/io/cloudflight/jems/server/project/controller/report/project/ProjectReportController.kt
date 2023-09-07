package io.cloudflight.jems.server.project.controller.report.project

import io.cloudflight.jems.api.plugin.dto.PreConditionCheckResultDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportStatusDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportSummaryDTO
import io.cloudflight.jems.api.project.dto.report.project.ProjectReportUpdateDTO
import io.cloudflight.jems.api.project.report.project.ProjectReportApi
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.toDTO
import io.cloudflight.jems.server.project.service.report.project.base.createProjectReport.CreateProjectReportInteractor
import io.cloudflight.jems.server.project.service.report.project.base.deleteProjectReport.DeleteProjectReportInteractor
import io.cloudflight.jems.server.project.service.report.project.base.finalizeVerification.FinalizeVerificationProjectReportInteractor
import io.cloudflight.jems.server.project.service.report.project.base.getMyProjectReports.GetMyProjectReportsInteractor
import io.cloudflight.jems.server.project.service.report.project.base.getProjectReport.GetProjectReportInteractor
import io.cloudflight.jems.server.project.service.report.project.base.getProjectReportList.GetProjectReportListInteractor
import io.cloudflight.jems.server.project.service.report.project.base.runProjectReportPreSubmissionCheck.RunProjectReportPreSubmissionCheck
import io.cloudflight.jems.server.project.service.report.project.base.startVerificationProjectReport.StartVerificationProjectReportInteractor
import io.cloudflight.jems.server.project.service.report.project.base.submitProjectReport.SubmitProjectReportInteractor
import io.cloudflight.jems.server.project.service.report.project.base.updateProjectReport.UpdateProjectReportInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportController(
    private val getReportList: GetProjectReportListInteractor,
    private val getReport: GetProjectReportInteractor,
    private val createReport: CreateProjectReportInteractor,
    private val updateReport: UpdateProjectReportInteractor,
    private val deleteReport: DeleteProjectReportInteractor,
    private val runProjectReportPreSubmissionCheck: RunProjectReportPreSubmissionCheck,
    private val submitReport: SubmitProjectReportInteractor,
    private val startVerificationReport: StartVerificationProjectReportInteractor,
    private val finalizeVerificationProjectReport: FinalizeVerificationProjectReportInteractor,
    private val getMyProjectReports: GetMyProjectReportsInteractor
) : ProjectReportApi {

    override fun getProjectReportList(projectId: Long, pageable: Pageable) =
        getReportList.findAll(projectId, pageable).toDto()

    override fun getMyProjectReports(pageable: Pageable): Page<ProjectReportSummaryDTO> =
        getMyProjectReports.findAllOfMine(pageable).toDto()

    override fun getProjectReport(projectId: Long, reportId: Long) =
        getReport.findById(projectId, reportId = reportId).toDto()

    override fun createProjectReport(projectId: Long, data: ProjectReportUpdateDTO) =
        createReport.createReportFor(projectId, data.toModel()).toDto()

    override fun updateProjectReport(projectId: Long, reportId: Long, data: ProjectReportUpdateDTO) =
        updateReport.updateReport(projectId, reportId = reportId, data.toModel()).toDto()

    override fun deleteProjectReport(projectId: Long, reportId: Long) =
        deleteReport.delete(projectId, reportId = reportId)

    override fun runPreCheck(projectId: Long, reportId: Long): PreConditionCheckResultDTO =
        runProjectReportPreSubmissionCheck.preCheck(projectId, reportId).toDTO()

    override fun submitProjectReport(projectId: Long, reportId: Long): ProjectReportStatusDTO =
        submitReport.submit(projectId = projectId, reportId = reportId).toDto()

    override fun startVerificationOnProjectReport(projectId: Long, reportId: Long) =
        startVerificationReport.startVerification(projectId = projectId, reportId = reportId).toDto()

    override fun finalizeVerificationOnProjectReport(projectId: Long, reportId: Long) =
        finalizeVerificationProjectReport.finalizeVerification(reportId).toDto()
}
