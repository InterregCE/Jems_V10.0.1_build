package io.cloudflight.jems.server.project.service.report.partner.procurement.uploadFileToProjectPartnerReportProcurement

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata

interface UploadFileToProjectPartnerReportProcurementInteractor {

    fun uploadToProcurement(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        file: ProjectFile,
    ): ProjectReportFileMetadata

}
