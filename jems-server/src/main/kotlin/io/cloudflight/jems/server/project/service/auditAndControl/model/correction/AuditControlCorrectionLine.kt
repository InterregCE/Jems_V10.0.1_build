package io.cloudflight.jems.server.project.service.auditAndControl.model.correction

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.CorrectionImpactAction
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal

data class AuditControlCorrectionLine(
    val id: Long,
    val orderNr: Int,
    val status: AuditControlStatus,
    val type: AuditControlCorrectionType,
    val auditControlId: Long,
    val auditControlNr: Int,
    var canBeDeleted: Boolean,

    val partnerReport: Int?,
    val partnerId: Long?,
    var partnerRole: ProjectPartnerRole?,
    var partnerNumber: Int?,
    var partnerDisabled: Boolean,
    val lumpSumOrderNr: Int?,
    val followUpAuditNr: Int?,
    val followUpCorrectionNr: Int?,
    val fund: ProgrammeFund?,

    val fundAmount: BigDecimal,
    val publicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    var total: BigDecimal,

    val impactProjectLevel: CorrectionImpactAction,
    val scenario: ProjectCorrectionProgrammeMeasureScenario
)
