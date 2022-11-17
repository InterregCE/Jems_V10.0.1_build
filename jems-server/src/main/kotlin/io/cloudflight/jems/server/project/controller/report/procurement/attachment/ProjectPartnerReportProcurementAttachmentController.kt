package io.cloudflight.jems.server.project.controller.report.procurement.attachment

import io.cloudflight.jems.api.project.report.procurement.ProjectPartnerReportProcurementAttachmentApi
import io.cloudflight.jems.server.project.controller.report.toDto
import io.cloudflight.jems.server.project.controller.report.toProjectFile
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.getProjectPartnerReportProcurementAttachment.GetProjectPartnerReportProcurementAttachmentInteractor
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.uploadFileToProjectPartnerReportProcurementAttachment.UploadFileToProjectPartnerReportProcurementAttachmentInteractor
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectPartnerReportProcurementAttachmentController(
    private val getAttachment: GetProjectPartnerReportProcurementAttachmentInteractor,
    private val uploadFile: UploadFileToProjectPartnerReportProcurementAttachmentInteractor,
) : ProjectPartnerReportProcurementAttachmentApi {

    override fun getAttachments(partnerId: Long, reportId: Long, procurementId: Long) =
        getAttachment.getAttachment(partnerId = partnerId, reportId = reportId, procurementId).map { it.toDto() }

    override fun uploadAttachment(partnerId: Long, reportId: Long, procurementId: Long, file: MultipartFile) =
        uploadFile
            .uploadToProcurement(partnerId = partnerId, reportId = reportId, procurementId, file.toProjectFile())
            .toDto()

}
