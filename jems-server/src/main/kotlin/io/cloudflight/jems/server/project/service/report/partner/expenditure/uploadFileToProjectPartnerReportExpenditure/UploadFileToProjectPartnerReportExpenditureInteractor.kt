package io.cloudflight.jems.server.project.service.report.partner.expenditure.uploadFileToProjectPartnerReportExpenditure

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata

interface UploadFileToProjectPartnerReportExpenditureInteractor {
    fun uploadToExpenditure(
        partnerId: Long,
        reportId: Long,
        expenditureId: Long,
        file: ProjectFile
    ): ProjectReportFileMetadata
}
