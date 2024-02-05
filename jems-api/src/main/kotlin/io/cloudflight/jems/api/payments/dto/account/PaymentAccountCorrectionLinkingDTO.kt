package io.cloudflight.jems.api.payments.dto.account

import io.cloudflight.jems.api.project.dto.auditAndControl.ControllingBodyDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureScenarioDTO
import java.math.BigDecimal

data class PaymentAccountCorrectionLinkingDTO(
    val correction: AuditControlCorrectionDTO,

    val projectId: Long,
    val projectAcronym: String,
    val projectCustomIdentifier: String,
    val priorityAxis: String,
    val controllingBody: ControllingBodyDTO,
    val scenario: ProjectCorrectionProgrammeMeasureScenarioDTO,
    val projectFlagged94Or95: Boolean,
    val paymentAccountId: Long?,

    val fundAmount: BigDecimal,
    val correctedFundAmount: BigDecimal,
    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
    val comment: String?,
)
