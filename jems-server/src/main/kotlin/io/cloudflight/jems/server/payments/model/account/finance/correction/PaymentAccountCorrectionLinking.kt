package io.cloudflight.jems.server.payments.model.account.finance.correction

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import java.math.BigDecimal

data class PaymentAccountCorrectionLinking(
    val correction: AuditControlCorrection,
    val projectId: Long,
    val projectAcronym: String,
    val projectCustomIdentifier: String,
    val priorityAxis: String,
    val controllingBody: ControllingBody,
    val scenario: ProjectCorrectionProgrammeMeasureScenario,
    val paymentAccountId: Long?,

    val fundAmount: BigDecimal,
    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
    val comment: String?,
)
