package io.cloudflight.jems.server.payments.model.account

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import java.math.BigDecimal

data class PaymentAccountCorrectionTmp(
    val correctionEntity: AuditControlCorrectionEntity,
    val projectId: Long,
    val projectAcronym: String,
    val projectCustomIdentifier: String,
    val priorityAxis: String?,
    val controllingBody: ControllingBody,

    val isProjectFlagged94Or95: Boolean,
    val paymentAccountId: Long?,

    val fundAmount: BigDecimal,
    val correctedFundAmount: BigDecimal,
    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
    val comment: String?,
    val scenario: ProjectCorrectionProgrammeMeasureScenario,
)
