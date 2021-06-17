package io.cloudflight.jems.api.project.dto.associatedorganization

import io.cloudflight.jems.api.project.dto.InputOrganization
import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.InputTranslation

data class InputProjectAssociatedOrganization (

    val id: Long?,

    val partnerId: Long,

    override val nameInOriginalLanguage: String? = null,

    override val nameInEnglish: String? = null,

    val address: InputProjectAssociatedOrganizationAddress? = null,

    val contacts: Set<InputProjectContact> = emptySet(),

    val roleDescription: Set<InputTranslation> = emptySet()

): InputOrganization
