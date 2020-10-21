package io.cloudflight.jems.api.project.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class InputProjectContact (

    @field:Size(max = 25, message = "project.contact.title.size.too.long")
    val title: String? = null,

    @field:NotNull(message = "project.contact.type.should.not.be.empty")
    val type: ProjectContactType,

    @field:Size(max = 50, message = "project.contact.first.name.size.too.long")
    val firstName: String? = null,

    @field:Size(max = 50, message = "project.contact.last.name.size.too.long")
    val lastName: String? = null,

    @field:Size(max = 255, message = "project.contact.email.size.too.long")
    @field:Email(message = "project.contact.email.wrong.format")
    val email: String? = null,

    @field:Size(max = 25, message = "project.contact.telephone.size.too.long")
    @field:Pattern(regexp = "^[0-9+()/-]*$", message = "project.contact.telephone.wrong.format")
    val telephone: String? = null
)
