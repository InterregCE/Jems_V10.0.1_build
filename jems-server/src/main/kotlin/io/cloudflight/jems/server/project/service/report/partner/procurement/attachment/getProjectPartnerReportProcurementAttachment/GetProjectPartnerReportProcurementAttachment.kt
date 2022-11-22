package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.getProjectPartnerReportProcurementAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportProcurementAttachment(
    private val service: GetProjectPartnerReportProcurementAttachmentService,
) : GetProjectPartnerReportProcurementAttachmentInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportProcurementAttachmentException::class)
    override fun getAttachment(partnerId: Long, reportId: Long, procurementId: Long) =
        service.getAttachment(partnerId, reportId = reportId, procurementId = procurementId)

}
