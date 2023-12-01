package io.cloudflight.jems.api.project.dto.auditAndControl.correction

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.CorrectionImpactActionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureScenarioDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.math.BigDecimal

data class ProjectAuditControlCorrectionLineDTO(
    val id: Long,
    val orderNr: Int,
    val status: AuditStatusDTO,
    val type: AuditControlCorrectionTypeDTO,
    val auditControlId: Long,
    val auditControlNr: Int,
    val canBeDeleted: Boolean,

    val partnerReport: Int?,
    val partnerRole: ProjectPartnerRoleDTO?,
    val partnerNumber: Int?,
    val partnerDisabled: Boolean?,
    val followUpAuditNr: Int?,
    val followUpCorrectionNr: Int?,
    val fundType: ProgrammeFundTypeDTO?,
    val fundAmount: BigDecimal,
    val publicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val total: BigDecimal,
    val impactProjectLevel: CorrectionImpactActionDTO,
    val scenario: ProjectCorrectionProgrammeMeasureScenarioDTO
)
