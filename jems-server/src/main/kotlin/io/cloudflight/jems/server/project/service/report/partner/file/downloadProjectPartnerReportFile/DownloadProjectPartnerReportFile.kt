package io.cloudflight.jems.server.project.service.report.partner.file.downloadProjectPartnerReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadProjectPartnerReportFile(
    private val reportFilePersistence: ProjectReportFilePersistence,
) : DownloadProjectPartnerReportFileInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadProjectPartnerReportFileException::class)
    override fun download(partnerId: Long, fileId: Long) =
        reportFilePersistence.downloadFile(partnerId = partnerId, fileId = fileId)
            ?: throw FileNotFound()

}
