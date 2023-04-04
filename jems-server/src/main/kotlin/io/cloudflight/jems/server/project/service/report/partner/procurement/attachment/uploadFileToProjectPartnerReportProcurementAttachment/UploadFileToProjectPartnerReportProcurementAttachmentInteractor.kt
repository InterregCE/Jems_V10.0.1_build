package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.uploadFileToProjectPartnerReportProcurementAttachment

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadFileToProjectPartnerReportProcurementAttachmentInteractor {

    fun uploadToProcurement(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

}
