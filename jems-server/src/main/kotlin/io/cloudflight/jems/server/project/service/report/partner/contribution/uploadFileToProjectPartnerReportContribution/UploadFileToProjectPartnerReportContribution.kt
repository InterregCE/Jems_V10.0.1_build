package io.cloudflight.jems.server.project.service.report.partner.contribution.uploadFileToProjectPartnerReportContribution

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.file.ProjectPartnerReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadFileToProjectPartnerReportContribution(
    private val reportFilePersistence: ProjectPartnerReportFilePersistence,
    private val reportContributionPersistence: ProjectPartnerReportContributionPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val securityService: SecurityService,
) : UploadFileToProjectPartnerReportContributionInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UploadFileToProjectPartnerReportContributionException::class)
    override fun uploadToContribution(
        partnerId: Long,
        reportId: Long,
        contributionId: Long,
        file: ProjectFile
    ): JemsFileMetadata {
        if (!reportContributionPersistence.existsByContributionId(partnerId, reportId = reportId, contributionId = contributionId))
            throw ContributionNotFoundException(contributionId = contributionId)

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.Contribution) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val location = generatePath(projectId, partnerId, reportId, contributionId)

            return reportFilePersistence.updatePartnerReportContributionAttachment(
                contributionId = contributionId,
                file = file.getFileMetadata(projectId, partnerId, location, type = this, securityService.getUserIdOrThrow()),
            )
        }
    }

}
