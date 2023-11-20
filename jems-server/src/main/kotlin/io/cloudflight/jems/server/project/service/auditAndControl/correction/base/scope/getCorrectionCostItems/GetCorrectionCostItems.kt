package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.scope.getCorrectionCostItems

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionCostItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCorrectionCostItems(
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
): GetCorrectionCostItemsInteractor {


    @CanViewAuditControlCorrection
    @Transactional
    @ExceptionWrapper(GetCorrectionCostItemsException::class)
    override fun getCostItems(correctionId: Long, pageable: Pageable): Page<CorrectionCostItem> {
        val correction = auditControlCorrectionPersistence.getByCorrectionId(correctionId)

        if (!correction.isPartnerRepostSet()) {
            throw MandatoryScopeNotSetException()
        }
        return auditControlCorrectionPersistence.getCorrectionAvailableCostItems(correction.partnerReportId!!, pageable)
    }

}