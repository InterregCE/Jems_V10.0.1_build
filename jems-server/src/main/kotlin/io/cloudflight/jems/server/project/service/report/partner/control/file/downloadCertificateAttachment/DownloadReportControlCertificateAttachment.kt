package io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificateAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadReportControlCertificateAttachment(
    private val filePersistence: JemsFilePersistence,
    private val projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence,
) : DownloadReportControlCertificateAttachmentInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadReportControlCertificateAttachmentException::class)
    override fun downloadReportControlCertificateAttachment(partnerId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray>  {
        val certificate = projectPartnerReportControlFilePersistence.getByReportIdAndId(reportId, fileId)
        return filePersistence.downloadFile(JemsFileType.ControlDocument, certificate.signedFile!!.id)!!
    }
}
