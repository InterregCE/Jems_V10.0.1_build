package io.cloudflight.jems.server.project.service.auditAndControl.correction.finance.getProjectCorrectionFinancialDescription

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.finance.AuditControlCorrectionFinance

interface GetProjectCorrectionFinancialDescriptionInteractor {

    fun getCorrectionFinancialDescription(correctionId: Long): AuditControlCorrectionFinance

}
