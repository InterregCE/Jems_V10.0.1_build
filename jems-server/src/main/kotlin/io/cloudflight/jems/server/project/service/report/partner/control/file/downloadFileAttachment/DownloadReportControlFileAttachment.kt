package io.cloudflight.jems.server.project.service.report.partner.control.file.downloadFileAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadReportControlFileAttachment(
    private val filePersistence: JemsFilePersistence,
    private val projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence,
) : DownloadReportControlFileAttachmentInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadReportControlFileAttachmentException::class)
    override fun downloadReportControlCertificateAttachment(partnerId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray>  {
        val controlFile = projectPartnerReportControlFilePersistence.getByReportIdAndId(reportId, fileId)
        return filePersistence.downloadFile(partnerId, controlFile.signedFile!!.id)!!
    }
}
