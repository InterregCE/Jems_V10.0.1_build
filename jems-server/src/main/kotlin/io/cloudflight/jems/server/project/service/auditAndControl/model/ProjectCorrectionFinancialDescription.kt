package io.cloudflight.jems.server.project.service.auditAndControl.model

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectCorrectionFinancialDescription (
    val correctionId: Long,
    val deduction: Boolean = true,
    val fundAmount: BigDecimal = BigDecimal.ZERO,
    val publicContribution: BigDecimal = BigDecimal.ZERO,
    val autoPublicContribution: BigDecimal = BigDecimal.ZERO,
    val privateContribution: BigDecimal = BigDecimal.ZERO,
    val infoSentBeneficiaryDate: LocalDate? = null,
    val infoSentBeneficiaryComment: String? = null,
    val correctionType: CorrectionType? = null,
    val clericalTechnicalMistake: Boolean = false,
    val goldPlating: Boolean = false,
    val suspectedFraud: Boolean = false,
    val correctionComment: String? = null,
)
