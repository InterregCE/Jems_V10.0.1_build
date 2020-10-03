package io.cloudflight.jems.api.programme.dto

import javax.validation.constraints.NotBlank

data class InputProgrammePriorityIdentifier(

    @field:NotBlank(message = "programme.priority.code.should.not.be.empty")
    val code: String?

)
