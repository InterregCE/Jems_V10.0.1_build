package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.uploadFileToProjectPartnerReportProcurementGdprAttachment

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadFileToProjectPartnerReportProcurementGdprAttachmentInteractor {

    fun uploadToGdprProcurement(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

}
