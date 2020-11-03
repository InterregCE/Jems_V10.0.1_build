package io.cloudflight.jems.api.programme.dto

import javax.validation.constraints.NotBlank

data class InputProgrammeLanguage(

    @field:NotBlank(message = "programme.language.code.should.not.be.empty")
    val code: SystemLanguage,

    val ui: Boolean = false,

    val fallback: Boolean = false,

    val input: Boolean = false
)
