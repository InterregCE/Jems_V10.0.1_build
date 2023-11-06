package io.cloudflight.jems.server.project.service.auditAndControl.model

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectCorrectionFinancialDescriptionUpdate (
    val deduction: Boolean,
    val fundAmount: BigDecimal,
    val publicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val infoSentBeneficiaryDate: LocalDate?,
    val infoSentBeneficiaryComment: String?,
    val correctionType: CorrectionType,
    val clericalTechnicalMistake: Boolean,
    val goldPlating: Boolean,
    val suspectedFraud: Boolean,
    val correctionComment: String?,
)
