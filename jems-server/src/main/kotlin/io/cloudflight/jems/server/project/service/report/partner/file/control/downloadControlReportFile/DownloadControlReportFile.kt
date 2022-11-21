package io.cloudflight.jems.server.project.service.report.partner.file.control.downloadControlReportFile

import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReportFile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadControlReportFile(
    private val filePersistence: JemsFilePersistence
) : DownloadControlReportFileInteractor {

    @CanViewPartnerControlReportFile
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadControlReportFileException::class)
    override fun download(partnerId: Long, fileId: Long) =
        filePersistence.downloadFile(partnerId = partnerId, fileId = fileId)
            ?: throw FileNotFound()

}
