package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

import io.cloudflight.jems.server.payments.model.ec.AccountingYear


data class ProjectCorrectionProgrammeMeasure(
    val correctionId: Long,
    val scenario: ProjectCorrectionProgrammeMeasureScenario,
    val comment: String?,
    val includedInAccountingYear: AccountingYear?,
)
