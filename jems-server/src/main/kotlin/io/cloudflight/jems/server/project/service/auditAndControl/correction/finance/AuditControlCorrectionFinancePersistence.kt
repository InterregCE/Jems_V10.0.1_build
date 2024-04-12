package io.cloudflight.jems.server.project.service.auditAndControl.correction.finance

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.finance.AuditControlCorrectionFinance
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.finance.AuditControlCorrectionFinanceUpdate

interface AuditControlCorrectionFinancePersistence {

    fun getCorrectionFinancialDescription(correctionId: Long): AuditControlCorrectionFinance

    fun updateCorrectionFinancialDescription(
        correctionId: Long,
        financialDescription: AuditControlCorrectionFinanceUpdate
    ) : AuditControlCorrectionFinance
}
