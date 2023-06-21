package io.cloudflight.jems.server.project.service.report.partner.expenditure.uploadFileToProjectPartnerReportExpenditure

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadFileToProjectPartnerReportExpenditureInteractor {
    fun uploadToExpenditure(
        partnerId: Long,
        reportId: Long,
        expenditureId: Long,
        file: ProjectFile
    ): JemsFileMetadata
}
