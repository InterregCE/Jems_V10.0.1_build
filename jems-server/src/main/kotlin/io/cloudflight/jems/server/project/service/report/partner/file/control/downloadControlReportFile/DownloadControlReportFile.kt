package io.cloudflight.jems.server.project.service.report.partner.file.control.downloadControlReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReportFile
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadControlReportFile(
    private val reportFilePersistence: ProjectReportFilePersistence,
) : DownloadControlReportFileInteractor {

    @CanViewPartnerControlReportFile
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadControlReportFileException::class)
    override fun download(partnerId: Long, fileId: Long) =
        reportFilePersistence.downloadFile(partnerId = partnerId, fileId = fileId)
            ?: throw FileNotFound()

}
