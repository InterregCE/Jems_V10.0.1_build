package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerContact

data class OutputProjectAssociatedOrganizationDetail (
    val id: Long,
    val nameInOriginalLanguage: String? = null,
    val nameInEnglish: String? = null,
    val organizationAddress: OutputProjectAssociatedOrganizationAddressDetails? = null,
    val sortNumber: Int? = null,
    val partner: OutputProjectPartner,
    val associatedOrganizationContacts: Set<OutputProjectPartnerContact>?
)
