package io.cloudflight.jems.server.project.service.report.partner.file.control.downloadControlReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReportFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadControlReportFile(
    private val partnerPersistence: PartnerPersistence,
    private val filePersistence: JemsFilePersistence,
) : DownloadControlReportFileInteractor {

    @CanViewPartnerControlReportFile
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadControlReportFileException::class)
    override fun download(partnerId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray> {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        // to make sure fileId corresponds to correct report, we need to verify it through location path
        val reportPrefix = JemsFileType.PartnerControlReport.generatePath(projectId, partnerId, reportId)

        return filePersistence.existsFile(partnerId = partnerId, pathPrefix = reportPrefix, fileId = fileId)
            .let { exists -> if (exists) filePersistence.downloadFile(partnerId = partnerId, fileId = fileId) else null }
            ?: throw FileNotFound()
    }

}
