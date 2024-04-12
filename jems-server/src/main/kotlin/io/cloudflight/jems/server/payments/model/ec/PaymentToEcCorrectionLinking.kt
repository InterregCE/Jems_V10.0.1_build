package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import java.math.BigDecimal

data class PaymentToEcCorrectionLinking(
    val correction: AuditControlCorrection,
    val projectId: Long,
    val projectAcronym: String,
    val projectCustomIdentifier: String,
    val priorityAxis: String,
    val controllingBody: ControllingBody,
    val scenario: ProjectCorrectionProgrammeMeasureScenario,
    val projectFlagged94Or95: Boolean,
    val paymentToEcId: Long?,

    val fundAmount: BigDecimal,
    var correctedFundAmount: BigDecimal,
    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
    val comment: String?,

    val totalEligibleWithoutArt94or95: BigDecimal,
    var correctedTotalEligibleWithoutArt94or95: BigDecimal,
    val unionContribution: BigDecimal,
    var correctedUnionContribution: BigDecimal,
)
