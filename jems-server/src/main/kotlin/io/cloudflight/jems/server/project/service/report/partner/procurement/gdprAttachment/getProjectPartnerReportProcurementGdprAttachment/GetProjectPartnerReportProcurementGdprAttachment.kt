package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.getProjectPartnerReportProcurementGdprAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportProcurementGdprAttachment(
    private val service: GetProjectPartnerReportProcurementGdprAttachmentService,
) : GetProjectPartnerReportProcurementGdprAttachmentInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportProcurementAttachmentException::class)
    override fun getGdprAttachment(partnerId: Long, reportId: Long, procurementId: Long) =
        service.getAttachment(partnerId, reportId = reportId, procurementId = procurementId)

}
