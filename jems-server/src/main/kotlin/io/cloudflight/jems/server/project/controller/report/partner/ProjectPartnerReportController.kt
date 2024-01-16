package io.cloudflight.jems.server.project.controller.report.partner

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.plugin.dto.PreConditionCheckResultDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileSearchRequestDTO
import io.cloudflight.jems.api.project.dto.report.partner.ProjectPartnerReportSummaryDTO
import io.cloudflight.jems.api.project.dto.report.partner.ReportStatusDTO
import io.cloudflight.jems.api.project.report.partner.ProjectPartnerReportApi
import io.cloudflight.jems.server.common.toResponseFile
import io.cloudflight.jems.server.project.controller.partner.toDto
import io.cloudflight.jems.server.project.controller.toDTO
import io.cloudflight.jems.server.project.service.report.partner.base.canCreateProjectPartnerReport.CanCreateProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.base.createProjectPartnerReport.CreateProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.base.deleteProjectPartnerReport.DeleteProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.base.finalizeControlPartnerReport.FinalizeControlPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.base.getMyProjectPartnerReports.GetMyProjectPartnerReportsInteractor
import io.cloudflight.jems.server.project.service.report.partner.base.getProjectPartnerReport.GetProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.base.getProjectReportPartnerList.GetProjectReportPartnerListInteractor
import io.cloudflight.jems.server.project.service.report.partner.base.reOpenControlPartnerReport.ReOpenControlPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.base.reOpenProjectPartnerReport.ReOpenProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.base.runPartnerReportPreSubmissionCheck.RunPartnerReportPreSubmissionCheckInteractor
import io.cloudflight.jems.server.project.service.report.partner.base.startControlPartnerReport.StartControlPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport.SubmitProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.control.overview.runControlPartnerReportPreSubmissionCheck.RunControlPartnerReportPreSubmissionCheckInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.control.deleteControlReportFile.DeleteControlReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.control.downloadControlReportFile.DownloadControlReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.control.listProjectPartnerControlReportFile.ListControlReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.control.setDescriptionToControlReportFile.SetDescriptionToControlReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.control.uploadFileToControlReport.UploadFileToControlReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.deleteProjectPartnerReportFile.DeleteProjectPartnerReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.downloadProjectPartnerReportFile.DownloadProjectPartnerReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile.ListProjectPartnerReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.setDescriptionToFile.SetDescriptionToProjectPartnerReportFileInteractor
import io.cloudflight.jems.server.project.service.report.partner.file.uploadFileToProjectPartnerReport.UploadFileToProjectPartnerReportInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectPartnerReportController(
    private val getPartnerList: GetProjectReportPartnerListInteractor,
    private val getPartnerReport: GetProjectPartnerReportInteractor,
    private val createPartnerReport: CreateProjectPartnerReportInteractor,
    private val canCreatePartnerReport: CanCreateProjectPartnerReportInteractor,
    private val runPreCheckPartnerReport: RunPartnerReportPreSubmissionCheckInteractor,
    private val submitPartnerReport: SubmitProjectPartnerReportInteractor,
    private val reOpenPartnerReport: ReOpenProjectPartnerReportInteractor,
    private val reOpenControlPartnerReport: ReOpenControlPartnerReportInteractor,
    private val startControlReport: StartControlPartnerReportInteractor,
    private val runPreCheckPartnerControlReport: RunControlPartnerReportPreSubmissionCheckInteractor,
    private val finalizeControlReport: FinalizeControlPartnerReportInteractor,
    private val downloadReportFile: DownloadProjectPartnerReportFileInteractor,
    private val deleteReportFile: DeleteProjectPartnerReportFileInteractor,
    private val setDescriptionToReportFile: SetDescriptionToProjectPartnerReportFileInteractor,
    private val listPartnerReportFile: ListProjectPartnerReportFileInteractor,
    private val uploadPartnerReportFile: UploadFileToProjectPartnerReportInteractor,
    private val downloadControlReportFile: DownloadControlReportFileInteractor,
    private val deleteControlReportFile: DeleteControlReportFileInteractor,
    private val setDescriptionToControlReportFile: SetDescriptionToControlReportFileInteractor,
    private val listPartnerControlReportFile: ListControlReportFileInteractor,
    private val uploadPartnerControlReportFile: UploadFileToControlReportInteractor,
    private val deleteProjectPartnerReport: DeleteProjectPartnerReportInteractor,
    private val getMyProjectPartnerReports: GetMyProjectPartnerReportsInteractor
) : ProjectPartnerReportApi {

    override fun getProjectPartnersForReporting(projectId: Long, sort: Sort, version: String?) =
        getPartnerList.findAllByProjectId(projectId, sort, version).toDto()

    override fun getProjectPartnerReports(
        partnerId: Long,
        pageable: Pageable,
    ): Page<ProjectPartnerReportSummaryDTO> =
        getPartnerReport.findAll(partnerId = partnerId, pageable = pageable).toDto()

    override fun getMyProjectPartnerReports(pageable: Pageable): Page<ProjectPartnerReportSummaryDTO> =
        getMyProjectPartnerReports.findAllOfMine(pageable).toDto()

    override fun getProjectPartnerReport(partnerId: Long, reportId: Long) =
        getPartnerReport.findById(partnerId = partnerId, reportId = reportId).toDto()

    override fun canReportBeCreated(partnerId: Long): Boolean =
        canCreatePartnerReport.canCreateReportFor(partnerId)

    override fun createProjectPartnerReport(partnerId: Long) =
        createPartnerReport.createReportFor(partnerId = partnerId).toDto()

    override fun runPreCheck(partnerId: Long, reportId: Long): PreConditionCheckResultDTO =
        runPreCheckPartnerReport.preCheck(partnerId = partnerId, reportId = reportId).toDTO()

    override fun submitProjectPartnerReport(partnerId: Long, reportId: Long): ReportStatusDTO =
        submitPartnerReport.submit(partnerId = partnerId, reportId = reportId).toDto()

    override fun reOpenProjectPartnerReport(partnerId: Long, reportId: Long): ReportStatusDTO =
        reOpenPartnerReport.reOpen(partnerId = partnerId, reportId = reportId).toDto()

    override fun reOpenControlPartnerReport(partnerId: Long, reportId: Long): ReportStatusDTO =
        reOpenControlPartnerReport.reOpen(partnerId = partnerId, reportId = reportId).toDto()

    override fun startControlOnPartnerReport(partnerId: Long, reportId: Long) =
        startControlReport.startControl(partnerId = partnerId, reportId = reportId).toDto()

    override fun runPreCheckOnControlReport(partnerId: Long, reportId: Long): PreConditionCheckResultDTO =
        runPreCheckPartnerControlReport.preCheck(partnerId = partnerId, reportId = reportId).toDTO()

    override fun finalizeControlOnPartnerReport(partnerId: Long, reportId: Long) =
        finalizeControlReport.finalizeControl(partnerId = partnerId, reportId = reportId).toDto()

    override fun downloadReportFile(
        partnerId: Long,
        fileId: Long
    ): ResponseEntity<ByteArrayResource> =
        downloadReportFile.download(partnerId, fileId = fileId).toResponseFile()

    override fun deleteReportFile(partnerId: Long, reportId: Long, fileId: Long) =
        deleteReportFile.delete(partnerId = partnerId, reportId = reportId, fileId = fileId)

    override fun updateReportFileDescription(partnerId: Long, reportId: Long, fileId: Long, description: String?) =
        setDescriptionToReportFile.setDescription(partnerId = partnerId, reportId = reportId, fileId = fileId, description ?: "")

    override fun uploadReportFile(partnerId: Long, reportId: Long, file: MultipartFile) =
        uploadPartnerReportFile
            .uploadToReport(partnerId, reportId, file.toProjectFile())
            .toDto()

    override fun listReportFiles(
        partnerId: Long,
        pageable: Pageable,
        searchRequest: ProjectReportFileSearchRequestDTO,
    ): Page<JemsFileDTO> =
        listPartnerReportFile.list(
            partnerId = partnerId,
            pageable = pageable,
            searchRequest = searchRequest.toModel(),
        ).map { it.toDto() }

    override fun downloadControlReportFile(
        partnerId: Long,
        reportId: Long,
        fileId: Long
    ): ResponseEntity<ByteArrayResource> =
        downloadControlReportFile.download(partnerId, reportId = reportId, fileId = fileId).toResponseFile()

    override fun deleteControlReportFile(partnerId: Long, reportId: Long, fileId: Long) =
        deleteControlReportFile.delete(partnerId = partnerId, reportId = reportId, fileId = fileId)

    override fun updateControlReportFileDescription(partnerId: Long, reportId: Long, fileId: Long, description: String?) =
        setDescriptionToControlReportFile.setDescription(partnerId = partnerId, reportId = reportId, fileId = fileId, description ?: "")

    override fun uploadControlReportFile(partnerId: Long, reportId: Long, file: MultipartFile) =
        uploadPartnerControlReportFile
            .uploadToControlReport(partnerId, reportId, file.toProjectFile())
            .toDto()

    override fun listControlReportFiles(
        partnerId: Long,
        reportId: Long,
        pageable: Pageable,
    ): Page<JemsFileDTO> =
        listPartnerControlReportFile.list(
            partnerId = partnerId,
            reportId = reportId,
            pageable = pageable,
        ).map { it.toDto() }

    override fun deleteProjectPartnerReport(partnerId: Long, reportId: Long) {
        deleteProjectPartnerReport.delete(partnerId, reportId)
    }

}
