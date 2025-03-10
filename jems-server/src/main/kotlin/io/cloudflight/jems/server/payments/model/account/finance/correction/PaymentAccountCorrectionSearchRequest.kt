package io.cloudflight.jems.server.payments.model.account.finance.correction

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus

data class PaymentAccountCorrectionSearchRequest(
    val paymentAccountIds: Set<Long?>,
    val correctionStatus: AuditControlStatus,
    val scenarios: Set<ProjectCorrectionProgrammeMeasureScenario>,
    val fundIds: Set<Long>,
)
