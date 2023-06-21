package io.cloudflight.jems.server.project.service.report.partner.control.file.listFiles

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListReportControlFiles(
    private val projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence
): ListReportControlFilesInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListReportControlFilesException::class)
    override fun list(partnerId: Long, reportId: Long, pageable: Pageable) =
        projectPartnerReportControlFilePersistence.listReportControlFiles(reportId, pageable)

}
