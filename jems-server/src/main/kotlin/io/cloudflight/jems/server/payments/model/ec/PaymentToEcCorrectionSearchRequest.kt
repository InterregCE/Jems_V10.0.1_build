package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus

data class PaymentToEcCorrectionSearchRequest(
    val ecPaymentIds: Set<Long?>,
    val correctionStatus: AuditControlStatus,
    val scenarios: Set<ProjectCorrectionProgrammeMeasureScenario>,
    val fundIds: Set<Long>,
)
