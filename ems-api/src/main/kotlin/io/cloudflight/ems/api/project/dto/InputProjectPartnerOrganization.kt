package io.cloudflight.ems.api.project.dto

import javax.validation.constraints.Size

data class InputProjectPartnerOrganization (

    val id: Long? = null,

    @field:Size(max = 100, message = "partner.organization.original.name.size.too.long")
    val nameInOriginalLanguage: String? = null,

    @field:Size(max = 100, message = "partner.organization.english.name.size.too.long")
    val nameInEnglish: String? = null,

    @field:Size(max = 250, message = "partner.organization.department.size.too.long")
    val department: String? = null

)

