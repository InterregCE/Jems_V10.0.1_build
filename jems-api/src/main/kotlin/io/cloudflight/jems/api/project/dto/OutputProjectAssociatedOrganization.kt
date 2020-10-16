package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner


data class OutputProjectAssociatedOrganization (
    val id: Long,
    val nameInOriginalLanguage: String? = null,
    val nameInEnglish: String? = null,
    val organizationAddress: OutputProjectAssociatedOrganizationAddressDetails? = null,
    val sortNumber: Int? = null,
    val partner: OutputProjectPartner
)
