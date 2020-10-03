package io.cloudflight.jems.api.project.dto

import javax.validation.constraints.Size

data class InputProjectPartnerContribution (
    @field:Size(max = 2000, message = "partner.organization.relevance.size.too.long")
    val organizationRelevance: String? = null,

    @field:Size(max = 2000, message = "partner.organization.role.size.too.long")
    val organizationRole: String? = null,

    @field:Size(max = 2000, message = "partner.organization.experience.size.too.long")
    val organizationExperience: String? = null
)
