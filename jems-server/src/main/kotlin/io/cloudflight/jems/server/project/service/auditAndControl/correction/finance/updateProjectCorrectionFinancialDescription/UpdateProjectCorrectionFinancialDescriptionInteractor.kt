package io.cloudflight.jems.server.project.service.auditAndControl.correction.finance.updateProjectCorrectionFinancialDescription

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.finance.AuditControlCorrectionFinance
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.finance.AuditControlCorrectionFinanceUpdate

interface UpdateProjectCorrectionFinancialDescriptionInteractor {

    fun updateCorrectionFinancialDescription(
        correctionId: Long,
        correctionFinancialDescriptionUpdate: AuditControlCorrectionFinanceUpdate
    ): AuditControlCorrectionFinance
}
