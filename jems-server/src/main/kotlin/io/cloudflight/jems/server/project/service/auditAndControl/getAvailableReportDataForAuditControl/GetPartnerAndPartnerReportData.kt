package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewAuditControlForProject
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerAndPartnerReportData(
    private val service: GetPartnerAndPartnerReportDataService,
) : GetPartnerAndPartnerReportDataInteractor {

    @CanViewAuditControlForProject
    @Transactional
    @ExceptionWrapper(GetPartnerAndPartnerReportException::class)
    override fun getPartnerAndPartnerReportData(projectId: Long): List<CorrectionAvailablePartner> =
        service.getPartnerAndPartnerReportData(projectId)

}
