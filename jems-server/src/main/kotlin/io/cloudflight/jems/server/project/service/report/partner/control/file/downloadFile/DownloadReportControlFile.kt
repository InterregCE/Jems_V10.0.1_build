package io.cloudflight.jems.server.project.service.report.partner.control.file.downloadFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadReportControlFile(
    private val filePersistence: JemsFilePersistence,
    private val partnerPersistence: PartnerPersistence
) : DownloadReportControlFileInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadReportControlFileException::class)
    override fun download(partnerId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray> {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)

        // to make sure fileId corresponds to correct report, we need to verify it through location path
        val fileTypes = listOf(JemsFileType.ControlCertificate, JemsFileType.ControlReport)
        val fileDoesNotExist = fileTypes.none { fileType ->
            val pathPrefix = fileType.generatePath(projectId, partnerId, reportId)
            !filePersistence.existsFile(partnerId = partnerId, pathPrefix = pathPrefix, fileId = fileId)
        }

        if (fileDoesNotExist) throw FileNotFound()

        return filePersistence.downloadFile(partnerId = partnerId, fileId = fileId) ?: throw FileNotFound()
    }
}
