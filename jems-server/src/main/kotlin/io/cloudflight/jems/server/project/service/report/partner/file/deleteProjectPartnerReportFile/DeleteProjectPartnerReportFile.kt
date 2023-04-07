package io.cloudflight.jems.server.project.service.report.partner.file.deleteProjectPartnerReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.PartnerReport
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectPartnerReportFile(
    private val partnerPersistence: PartnerPersistence,
    private val filePersistence: JemsFilePersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val sensitiveDataAuthorization: SensitiveDataAuthorizationService
) : DeleteProjectPartnerReportFileInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(DeleteProjectPartnerReportFileException::class)
    override fun delete(partnerId: Long, reportId: Long, fileId: Long) {

        if(isGdprProtected(fileId = fileId, partnerId = partnerId) &&
            !sensitiveDataAuthorization.canEditPartnerSensitiveData(partnerId)) {
            throw SensitiveFileException()
        }

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        // to make sure fileId corresponds to correct report, we need to verify it through location path
        val reportPrefix = PartnerReport.generatePath(projectId, partnerId, reportId)

        if (!filePersistence.existsFile(partnerId = partnerId, pathPrefix = reportPrefix, fileId = fileId))
            throw FileNotFound()

        filePersistence.deleteFile(partnerId = partnerId, fileId = fileId)
    }

    private fun isGdprProtected(fileId: Long, partnerId: Long) =
        reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(partnerId = partnerId, fileId = fileId)

}
