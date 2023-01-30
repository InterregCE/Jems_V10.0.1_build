package io.cloudflight.jems.server.project.service.report.partner.control.file.listCertificates

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListReportControlCertificates(
    private val projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence
): ListReportControlCertificatesInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(ListReportControlCertificatesException::class)
    override fun list(partnerId: Long, reportId: Long, pageable: Pageable) =
        projectPartnerReportControlFilePersistence.listReportFiles(reportId, pageable)

}
