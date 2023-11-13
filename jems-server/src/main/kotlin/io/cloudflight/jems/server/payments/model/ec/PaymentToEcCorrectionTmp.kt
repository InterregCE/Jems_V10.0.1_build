package io.cloudflight.jems.server.payments.model.ec

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import java.math.BigDecimal

data class PaymentToEcCorrectionTmp(
    val correctionEntity: AuditControlCorrectionEntity,
    val projectId: Long,
    val projectAcronym: String,
    val projectCustomIdentifier: String,
    val priorityAxis: String?,
    val controllingBody: ControllingBody,

    val isProjectFlagged94Or95: Boolean,
    val paymentToEcId: Long?,

    val fundAmount: BigDecimal,
    val publicContribution: BigDecimal,
    val correctedPublicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val correctedAutoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val correctedPrivateContribution: BigDecimal,
    val comment: String?,
    val scenario: ProjectCorrectionProgrammeMeasureScenario
)
