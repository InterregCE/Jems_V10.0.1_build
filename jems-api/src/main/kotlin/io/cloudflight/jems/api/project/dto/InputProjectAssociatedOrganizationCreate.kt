package io.cloudflight.jems.api.project.dto

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class  InputProjectAssociatedOrganizationCreate (
    val id: Long? = null,

    @field:Size(max = 100, message = "partner.organization.original.name.size.too.long")
    val nameInOriginalLanguage: String? = null,

    @field:Size(max = 100, message = "partner.organization.english.name.size.too.long")
    val nameInEnglish: String? = null,

    val organizationAddress: InputProjectAssociatedOrganizationAddressDetails? = null,

    @field:NotNull(message = "project.partner.should.not.be.empty")
    val partnerId: Long,

    val associatedOrganizationContacts: Set<InputProjectPartnerContact>? = null
)
