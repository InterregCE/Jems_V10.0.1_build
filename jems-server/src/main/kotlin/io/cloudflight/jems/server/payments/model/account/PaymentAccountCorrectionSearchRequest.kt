package io.cloudflight.jems.server.payments.model.account

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus

data class PaymentAccountCorrectionSearchRequest(
    val paymentAccountIds: Set<Long?>,
    val correctionStatus: AuditControlStatus,
    val scenarios: List<ProjectCorrectionProgrammeMeasureScenario>,
    val fundIds: Set<Long>
)
