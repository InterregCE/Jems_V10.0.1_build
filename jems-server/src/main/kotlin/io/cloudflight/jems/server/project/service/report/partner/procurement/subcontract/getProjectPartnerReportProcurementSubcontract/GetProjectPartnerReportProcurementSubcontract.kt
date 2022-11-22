package io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.getProjectPartnerReportProcurementSubcontract

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportProcurementSubcontract(
    private val service: GetProjectPartnerReportProcurementSubcontractService,
) : GetProjectPartnerReportProcurementSubcontractInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportProcurementSubcontractException::class)
    override fun getSubcontract(partnerId: Long, reportId: Long, procurementId: Long) =
        service.getSubcontract(partnerId, reportId = reportId, procurementId = procurementId)

}
