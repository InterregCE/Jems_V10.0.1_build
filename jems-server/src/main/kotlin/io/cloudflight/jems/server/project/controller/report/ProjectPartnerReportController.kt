package io.cloudflight.jems.server.project.controller.report

import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportSummaryDTO
import io.cloudflight.jems.api.project.dto.report.ReportStatusDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileSearchRequestDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportApi
import io.cloudflight.jems.server.project.service.report.partner.workflow.createProjectPartnerReport.CreateProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.deleteProjectPartnerReportFile.DeleteProjectPartnerReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.downloadProjectPartnerReportFile.DownloadProjectPartnerReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile.ListProjectPartnerReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.uploadFileToProjectPartnerReport.UploadFileToProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.getProjectPartnerReport.GetProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.workflow.startControlPartnerReport.StartControlPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.workflow.submitProjectPartnerReport.SubmitProjectPartnerReportInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectPartnerReportController(
    private val createPartnerReport: CreateProjectPartnerReportInteractor,
    private val submitPartnerReport: SubmitProjectPartnerReportInteractor,
    private val startControlReport: StartControlPartnerReportInteractor,
    private val getPartnerReport: GetProjectPartnerReportInteractor,
    private val downloadPartnerReportFile: DownloadProjectPartnerReportFileInteractor,
    private val deletePartnerReportFile: DeleteProjectPartnerReportFileInteractor,
    private val listPartnerReportFile: ListProjectPartnerReportFileInteractor,
    private val uploadPartnerReportFile: UploadFileToProjectPartnerReportInteractor,
) : ProjectPartnerReportApi {

    override fun getProjectPartnerReports(
        partnerId: Long,
        pageable: Pageable,
    ): Page<ProjectPartnerReportSummaryDTO> =
        getPartnerReport.findAll(partnerId = partnerId, pageable = pageable).toDto()

    override fun getProjectPartnerReport(partnerId: Long, reportId: Long) =
        getPartnerReport.findById(partnerId = partnerId, reportId = reportId).toDto()

    override fun createProjectPartnerReport(partnerId: Long) =
        createPartnerReport.createReportFor(partnerId = partnerId).toDto()

    override fun submitProjectPartnerReport(partnerId: Long, reportId: Long): ReportStatusDTO =
        submitPartnerReport.submit(partnerId = partnerId, reportId = reportId).toDto()

    override fun startControlOnPartnerReport(partnerId: Long, reportId: Long) =
        startControlReport.startControl(partnerId = partnerId, reportId = reportId).toDto()

    override fun downloadAttachment(
        partnerId: Long,
        fileId: Long
    ): ResponseEntity<ByteArrayResource> =
        with(downloadPartnerReportFile.download(partnerId, fileId = fileId)) {
            ResponseEntity.ok()
                .contentLength(this.second.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
                .body(ByteArrayResource(this.second))
    }

    override fun deleteAttachment(partnerId: Long, fileId: Long) =
        deletePartnerReportFile.delete(partnerId = partnerId, fileId = fileId)

    override fun uploadAttachment(partnerId: Long, reportId: Long, file: MultipartFile) =
        uploadPartnerReportFile
            .uploadToReport(partnerId, reportId, file.toProjectFile())
            .toDto()

    override fun listAttachments(
        partnerId: Long,
        pageable: Pageable,
        searchRequest: ProjectReportFileSearchRequestDTO,
    ): Page<ProjectReportFileDTO> =
        listPartnerReportFile.list(
            partnerId = partnerId,
            pageable = pageable,
            searchRequest = searchRequest.toModel(),
        ).map { it.toDto() }

}
