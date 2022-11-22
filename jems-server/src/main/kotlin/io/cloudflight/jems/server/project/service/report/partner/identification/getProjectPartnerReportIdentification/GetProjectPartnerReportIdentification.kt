package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportIdentification(
    private val service: GetProjectPartnerReportIdentificationService,
) : GetProjectPartnerReportIdentificationInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportIdentificationException::class)
    override fun getIdentification(partnerId: Long, reportId: Long) =
        service.getIdentification(partnerId, reportId = reportId)

}
