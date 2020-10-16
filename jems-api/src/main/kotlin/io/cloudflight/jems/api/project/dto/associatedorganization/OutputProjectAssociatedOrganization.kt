package io.cloudflight.jems.api.project.dto.associatedorganization

import io.cloudflight.jems.api.project.dto.InputOrganization

data class OutputProjectAssociatedOrganization(

    val id: Long,
    val partnerAbbreviation: String,
    override val nameInOriginalLanguage: String? = null,
    override val nameInEnglish: String? = null,
    val sortNumber: Int? = null

) : InputOrganization
