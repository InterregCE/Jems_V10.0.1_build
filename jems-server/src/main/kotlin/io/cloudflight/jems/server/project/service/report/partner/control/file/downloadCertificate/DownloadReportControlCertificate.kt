package io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificate

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadReportControlCertificate(
    private val filePersistence: JemsFilePersistence,
    private val partnerPersistence: PartnerPersistence
) : DownloadReportControlCertificateInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadReportControlCertificateException::class)
    override fun downloadReportControlCertificate(partnerId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray>  {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        // to make sure fileId corresponds to correct report, we need to verify it through location path
        val filePathPrefix = JemsFileType.ControlCertificate.generatePath(projectId, partnerId, reportId)

        return filePersistence.existsFile(partnerId = partnerId, pathPrefix = filePathPrefix, fileId = fileId)
            .let { exists -> if (exists) filePersistence.downloadFile(partnerId = partnerId, fileId = fileId) else null }
            ?: throw FileNotFound()
    }
}
