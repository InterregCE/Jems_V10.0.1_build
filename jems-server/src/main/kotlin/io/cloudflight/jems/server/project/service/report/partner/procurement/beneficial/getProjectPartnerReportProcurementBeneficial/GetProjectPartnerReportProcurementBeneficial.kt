package io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.getProjectPartnerReportProcurementBeneficial

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportProcurementBeneficial(
    private val service: GetProjectPartnerReportProcurementBeneficialService,
) : GetProjectPartnerReportProcurementBeneficialInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportProcurementBeneficialException::class)
    override fun getBeneficialOwner(partnerId: Long, reportId: Long, procurementId: Long) =
        service.getBeneficialOwner(partnerId, reportId = reportId, procurementId = procurementId)

}
