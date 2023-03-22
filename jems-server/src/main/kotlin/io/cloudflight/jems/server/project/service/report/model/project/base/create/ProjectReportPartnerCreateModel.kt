package io.cloudflight.jems.server.project.service.report.model.project.base.create

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal

data class ProjectReportPartnerCreateModel(
    val partnerId: Long,
    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val partnerRole: ProjectPartnerRole,
    val country: String?,
    val previouslyReported: BigDecimal,
)
