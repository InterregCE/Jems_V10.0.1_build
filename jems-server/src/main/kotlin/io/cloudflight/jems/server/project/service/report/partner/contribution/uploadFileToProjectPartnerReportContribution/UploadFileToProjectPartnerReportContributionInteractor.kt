package io.cloudflight.jems.server.project.service.report.partner.contribution.uploadFileToProjectPartnerReportContribution

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface UploadFileToProjectPartnerReportContributionInteractor {

    fun uploadToContribution(
        partnerId: Long,
        reportId: Long,
        contributionId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

}
