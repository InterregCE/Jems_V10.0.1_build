package io.cloudflight.jems.api.project.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class InputProjectPartnerContact (

    @field:Size(max = 25, message = "partner.contact.title.size.too.long")
    val title: String?,

    @field:NotNull(message = "partner.contact.type.should.not.be.empty")
    val type: PartnerContactPersonType,

    @field:Size(max = 50, message = "partner.contact.first.name.size.too.long")
    val firstName: String?,

    @field:Size(max = 50, message = "partner.contact.last.name.size.too.long")
    val lastName: String?,

    @field:Size(max = 255, message = "partner.contact.email.size.too.long")
    @field:Email(message = "partner.contact.email.wrong.format")
    val email: String?,

    @field:Size(max = 25, message = "partner.contact.telephone.size.too.long")
    @field:Pattern(regexp = "^[0-9+()/-]*$", message = "partner.contact.telephone.wrong.format")
    val telephone: String?
)
