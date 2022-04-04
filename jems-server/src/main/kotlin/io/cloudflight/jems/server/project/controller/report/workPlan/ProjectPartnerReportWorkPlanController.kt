package io.cloudflight.jems.server.project.controller.report.workPlan

import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportWorkPlanApi
import io.cloudflight.jems.server.project.controller.report.toDto
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.partner.workPlan.getProjectPartnerWorkPlan.GetProjectPartnerReportWorkPlanInteractor
import io.cloudflight.jems.server.project.service.report.partner.workPlan.updateProjectPartnerWorkPlan.UpdateProjectPartnerReportWorkPlanInteractor
import io.cloudflight.jems.server.project.service.report.partner.workPlan.uploadFileToProjectPartnerReport.UploadFileToProjectPartnerReportWorkPlanInteractor
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectPartnerReportWorkPlanController(
    private val getPartnerReportWorkPlan: GetProjectPartnerReportWorkPlanInteractor,
    private val updatePartnerReportWorkPlan: UpdateProjectPartnerReportWorkPlanInteractor,
    private val uploadFileToPartnerReportWorkPlan: UploadFileToProjectPartnerReportWorkPlanInteractor,
) : ProjectPartnerReportWorkPlanApi {

    override fun getWorkPlan(partnerId: Long, reportId: Long): List<ProjectPartnerReportWorkPackageDTO> =
        getPartnerReportWorkPlan.getForPartner(partnerId = partnerId, reportId = reportId).toDto()

    override fun updateWorkPlan(
        partnerId: Long,
        reportId: Long,
        workPackages: List<UpdateProjectPartnerReportWorkPackageDTO>
    ): List<ProjectPartnerReportWorkPackageDTO> =
        updatePartnerReportWorkPlan.update(
            partnerId = partnerId,
            reportId = reportId,
            workPlan = workPackages.toModel(),
        ).toDto()

    override fun uploadFileToActivity(partnerId: Long, reportId: Long, workPackageId: Long, activityId: Long, file: MultipartFile) =
        uploadFileToPartnerReportWorkPlan.uploadToActivity(partnerId, reportId, workPackageId, activityId, file.toProjectFile())
            .toDto()

    override fun uploadFileToDeliverable(partnerId: Long, reportId: Long, workPackageId: Long, activityId: Long, deliverableId: Long, file: MultipartFile) =
        uploadFileToPartnerReportWorkPlan.uploadToDeliverable(partnerId, reportId, workPackageId, activityId, deliverableId, file.toProjectFile())
            .toDto()

    override fun uploadFileToOutput(partnerId: Long, reportId: Long, workPackageId: Long, outputId: Long, file: MultipartFile) =
        uploadFileToPartnerReportWorkPlan.uploadToOutput(partnerId, reportId, workPackageId, outputId, file.toProjectFile())
            .toDto()

    private fun MultipartFile.toProjectFile() = ProjectFile(inputStream, originalFilename ?: name, size)

}
