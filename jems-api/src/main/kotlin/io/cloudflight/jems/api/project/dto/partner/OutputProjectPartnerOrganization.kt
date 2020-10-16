package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerAddress

data class OutputProjectPartnerOrganization (
    val id: Long?,
    val nameInOriginalLanguage: String?,
    val nameInEnglish: String?,
    val department: String?,
    val organizationDetails: Set<OutputProjectPartnerAddress>?
)
