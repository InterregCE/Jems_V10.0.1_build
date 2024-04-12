package io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure

import io.cloudflight.jems.server.payments.model.ec.AccountingYear


data class AuditControlCorrectionMeasure(
    val correctionId: Long,
    val scenario: ProjectCorrectionProgrammeMeasureScenario,
    val comment: String?,
    val includedInAccountingYear: AccountingYear?,
)
