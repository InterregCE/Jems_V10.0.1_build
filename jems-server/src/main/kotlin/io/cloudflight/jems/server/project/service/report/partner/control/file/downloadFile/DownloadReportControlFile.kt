package io.cloudflight.jems.server.project.service.report.partner.control.file.downloadFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReportFile
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadReportControlFile(
    private val filePersistence: JemsFilePersistence,
    private val projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence,
) : DownloadReportControlFileInteractor {

    @CanViewPartnerControlReportFile
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadReportControlFileException::class)
    override fun download(partnerId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray> {
        val controlFile = projectPartnerReportControlFilePersistence.getByReportIdAndId(reportId, fileId)
        return filePersistence.downloadFile(partnerId, controlFile.generatedFile.id)!!
    }
}
