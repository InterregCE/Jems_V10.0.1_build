package io.cloudflight.jems.server.project.controller.report.procurement

import io.cloudflight.jems.api.project.dto.report.partner.procurement.UpdateProjectPartnerReportProcurementDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportProcurementApi
import io.cloudflight.jems.server.common.toIdNamePairDTO
import io.cloudflight.jems.server.project.controller.report.toDto
import io.cloudflight.jems.server.project.controller.report.toProjectFile
import io.cloudflight.jems.server.project.service.report.partner.procurement.getProjectPartnerReportProcurement.GetProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement.UpdateProjectPartnerReportProcurementInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.uploadFileToProjectPartnerReportProcurement.UploadFileToProjectPartnerReportProcurementInteractor
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectPartnerReportProcurementController(
    private val getProcurement: GetProjectPartnerReportProcurementInteractor,
    private val updateProcurement: UpdateProjectPartnerReportProcurementInteractor,
    private val uploadFileToProcurement: UploadFileToProjectPartnerReportProcurementInteractor,
) : ProjectPartnerReportProcurementApi {

    override fun getProcurement(partnerId: Long, reportId: Long) =
        getProcurement.getProcurement(partnerId = partnerId, reportId = reportId).toDto()

    override fun updateProcurement(
        partnerId: Long,
        reportId: Long,
        procurementData: List<UpdateProjectPartnerReportProcurementDTO>
    ) =
        updateProcurement.update(
            partnerId = partnerId,
            reportId = reportId,
            procurementNew = procurementData.toModel(),
        ).toDto()

    override fun getProcurementSelectorList(partnerId: Long, reportId: Long) =
        getProcurement.getProcurementsForSelector(partnerId = partnerId, reportId = reportId)
            .map { it.toIdNamePairDTO() }

    override fun uploadFileToProcurement(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        file: MultipartFile,
    ) =
        uploadFileToProcurement
            .uploadToProcurement(partnerId, reportId, procurementId = procurementId, file.toProjectFile())
            .toDto()

}
