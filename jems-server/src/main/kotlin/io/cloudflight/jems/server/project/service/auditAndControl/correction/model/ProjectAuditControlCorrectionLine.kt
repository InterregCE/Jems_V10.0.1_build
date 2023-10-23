package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal

data class ProjectAuditControlCorrectionLine(
    val id: Long,
    val auditControlId: Long,
    val orderNr: Int,
    val status: CorrectionStatus,
    val linkedToInvoice: Boolean,
    val auditControlNumber: Int,
    val canBeDeleted: Boolean,

    //These will be updated in the following stories
    val partnerRoleDTO: ProjectPartnerRole = ProjectPartnerRole.PARTNER,
    val partnerNumber: Int = 1,
    val partnerReport: String = "",
    val initialAuditNUmber: Int = 1,
    val initialCorrectionNumber: Int = 1,
    val fundName: String = "",
    val fundAmount: BigDecimal = BigDecimal.ZERO,
    val publicContribution: BigDecimal = BigDecimal.ZERO,
    val autoPublicContribution: BigDecimal = BigDecimal.ZERO,
    val privateContribution: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal = BigDecimal.ZERO,
    val impactProjectLevel: String = "",
    val scenario: Int = 1
)
