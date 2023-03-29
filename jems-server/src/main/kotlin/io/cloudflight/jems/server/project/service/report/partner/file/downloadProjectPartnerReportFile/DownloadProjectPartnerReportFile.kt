package io.cloudflight.jems.server.project.service.report.partner.file.downloadProjectPartnerReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadProjectPartnerReportFile(
    private val filePersistence: JemsFilePersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val sensitiveDataAuthorization: SensitiveDataAuthorizationService
) : DownloadProjectPartnerReportFileInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadProjectPartnerReportFileException::class)
    override fun download(partnerId: Long, fileId: Long): Pair<String, ByteArray> {


        if(isGdprProtected(fileId = fileId, partnerId = partnerId) &&
                !sensitiveDataAuthorization.canViewPartnerSensitiveData(partnerId)) {
            throw SensitiveFileException()
        }

        return filePersistence.downloadFile(partnerId = partnerId, fileId = fileId)
            ?: throw FileNotFound()
    }

    private fun isGdprProtected(fileId: Long, partnerId: Long) =
        reportExpenditurePersistence.existsByPartnerIdAndAttachmentIdAndGdprTrue(partnerId = partnerId, fileId = fileId)

}
