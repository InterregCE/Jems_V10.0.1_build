package io.cloudflight.jems.api.project.dto

data class OutputProjectAssociatedOrganizationDetail (
    val id: Long,
    val nameInOriginalLanguage: String? = null,
    val nameInEnglish: String? = null,
    val organizationAddress: OutputProjectAssociatedOrganizationAddressDetails? = null,
    val sortNumber: Int? = null,
    val partner: OutputProjectPartner,
    val associatedOrganizationContacts: Set<OutputProjectPartnerContact>?
)
