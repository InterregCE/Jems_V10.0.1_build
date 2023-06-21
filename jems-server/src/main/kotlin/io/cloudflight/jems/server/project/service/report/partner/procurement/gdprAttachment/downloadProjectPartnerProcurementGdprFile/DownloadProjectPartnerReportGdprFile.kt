package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.downloadProjectPartnerProcurementGdprFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadProjectPartnerReportGdprFile(
    private val filePersistence: JemsFilePersistence,
    private val sensitiveDataAuthorization: SensitiveDataAuthorizationService
) : DownloadProjectPartnerReportGdprFileInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadProjectPartnerReportFileException::class)
    override fun download(partnerId: Long, fileId: Long): Pair<String, ByteArray> {

        if(!sensitiveDataAuthorization.canViewPartnerSensitiveData(partnerId)) {
            throw SensitiveFileException()
        }

        return filePersistence.downloadFile(partnerId = partnerId, fileId = fileId)
            ?: throw FileNotFound()
    }
}
