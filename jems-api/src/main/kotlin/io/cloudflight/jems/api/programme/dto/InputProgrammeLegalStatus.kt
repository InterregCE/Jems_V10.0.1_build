package io.cloudflight.jems.api.programme.dto

import io.cloudflight.jems.api.programme.validator.InputProgrammeLegalStatusValidator

data class InputProgrammeLegalStatus (
    val id: Long? = null,

    val description: String? = null
)

@InputProgrammeLegalStatusValidator
data class InputProgrammeLegalStatusWrapper(
    val statuses: Collection<InputProgrammeLegalStatus>
)
