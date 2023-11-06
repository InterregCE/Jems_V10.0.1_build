package io.cloudflight.jems.api.project.dto.auditAndControl.correction

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.math.BigDecimal

data class ProjectAuditControlCorrectionLineDTO(
    val id: Long,
    val auditControlId: Long,
    val orderNr: Int,
    val status: CorrectionStatusDTO,
    val linkedToInvoice: Boolean,
    val auditControlNumber: Int,
    val canBeDeleted: Boolean,

    //These will be updated in the following stories
    val partnerRoleDTO: ProjectPartnerRoleDTO,
    val partnerNumber: Int,
    val partnerDisabled: Boolean,
    val partnerReport: String,
    val initialAuditNUmber: Int,
    val initialCorrectionNumber: Int,
    val fundName: String,
    val fundAmount: BigDecimal,
    val publicContribution: BigDecimal,
    val autoPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val total: BigDecimal,
    val impactProjectLevel: String,
    val scenario: Int
)
