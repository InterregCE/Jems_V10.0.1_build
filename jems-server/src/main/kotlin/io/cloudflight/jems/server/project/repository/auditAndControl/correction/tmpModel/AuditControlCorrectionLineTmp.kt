package io.cloudflight.jems.server.project.repository.auditAndControl.correction.tmpModel

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpactAction
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal

data class AuditControlCorrectionLineTmp(
    val correction: AuditControlCorrection,

    val partnerId: Long?,
    val lumpSumPartnerId: Long?,
    val partnerNumber: Int?,
    val partnerAbbreviation: String?,
    val partnerRole: ProjectPartnerRole?,
    val reportNr: Int?,
    val lumpSumOrderNr: Int?,

    val followUpAuditNr: Int?,
    val followUpCorrectionNr: Int?,

    val fund: ProgrammeFund?,

    val fundAmount: BigDecimal,
    val publicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,

    val impactProjectLevel: AuditControlCorrectionImpactAction,
    val scenario: ProjectCorrectionProgrammeMeasureScenario,
)
