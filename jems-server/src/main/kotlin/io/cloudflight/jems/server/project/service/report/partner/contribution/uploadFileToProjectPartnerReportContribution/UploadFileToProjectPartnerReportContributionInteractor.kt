package io.cloudflight.jems.server.project.service.report.partner.contribution.uploadFileToProjectPartnerReportContribution

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadFileToProjectPartnerReportContributionInteractor {

    fun uploadToContribution(
        partnerId: Long,
        reportId: Long,
        contributionId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

}
