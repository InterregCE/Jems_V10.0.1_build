package io.cloudflight.jems.api.project.dto.auditAndControl.correction

import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO


data class ProjectCorrectionProgrammeMeasureDTO(
    val correctionId: Long,
    val scenario: ProjectCorrectionProgrammeMeasureScenarioDTO,
    val comment: String?,
    val includedInAccountingYear: AccountingYearDTO?,
)
