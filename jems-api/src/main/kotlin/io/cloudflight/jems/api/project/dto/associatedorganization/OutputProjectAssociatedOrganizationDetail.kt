package io.cloudflight.jems.api.project.dto.associatedorganization

import io.cloudflight.jems.api.project.dto.InputOrganization
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerContact

data class OutputProjectAssociatedOrganizationDetail (

    val id: Long,
    val partner: OutputProjectPartner,
    override val nameInOriginalLanguage: String? = null,
    override val nameInEnglish: String? = null,
    val sortNumber: Int? = null,
    val address: OutputProjectAssociatedOrganizationAddress? = null,
    val contacts: List<OutputProjectPartnerContact> = emptyList(),
    val roleDescription: Set<InputTranslation> = emptySet()

): InputOrganization
