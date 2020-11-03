package io.cloudflight.jems.api.programme.dto.priority

import javax.validation.constraints.NotBlank

data class InputProgrammePriorityIdentifier(

    @field:NotBlank(message = "programme.priority.code.should.not.be.empty")
    val code: String?

)
