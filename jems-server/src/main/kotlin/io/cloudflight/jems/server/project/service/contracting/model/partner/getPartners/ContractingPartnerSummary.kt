package io.cloudflight.jems.server.project.service.contracting.model.partner.getPartners

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole

data class ContractingPartnerSummary(
    val id: Long,
    val abbreviation: String,
    var institutionName: String? = null,
    val active: Boolean,
    val role: ProjectPartnerRole,
    val sortNumber: Int? = null,
    val country: String? = null,
    val region: String? = null,
    var currencyCode: String? = null,
    val locked: Boolean,
)
