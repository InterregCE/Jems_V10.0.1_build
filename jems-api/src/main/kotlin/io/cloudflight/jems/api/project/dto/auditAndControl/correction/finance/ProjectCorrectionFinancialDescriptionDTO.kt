package io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectCorrectionFinancialDescriptionDTO (
    val correctionId: Long,
    val deduction: Boolean,
    val fundAmount: BigDecimal,
    val publicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val infoSentBeneficiaryDate: LocalDate?,
    val infoSentBeneficiaryComment: String?,
    val correctionType: CorrectionTypeDTO?,
    val clericalTechnicalMistake: Boolean,
    val goldPlating: Boolean,
    val suspectedFraud: Boolean,
    val correctionComment: String?,
)
