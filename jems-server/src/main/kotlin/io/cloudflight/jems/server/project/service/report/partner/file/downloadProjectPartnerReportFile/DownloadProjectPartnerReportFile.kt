package io.cloudflight.jems.server.project.service.report.partner.file.downloadProjectPartnerReportFile

import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadProjectPartnerReportFile(
    private val filePersistence: JemsFilePersistence
) : DownloadProjectPartnerReportFileInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadProjectPartnerReportFileException::class)
    override fun download(partnerId: Long, fileId: Long) =
        filePersistence.downloadFile(partnerId = partnerId, fileId = fileId)
            ?: throw FileNotFound()

}
