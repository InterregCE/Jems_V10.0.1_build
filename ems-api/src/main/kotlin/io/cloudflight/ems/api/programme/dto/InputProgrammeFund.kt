package io.cloudflight.ems.api.programme.dto

import io.cloudflight.ems.api.programme.validator.InputProgrammeFundValidator

data class InputProgrammeFund(

    val selected: Boolean = true,

    // fill in id only if selecting/unselecting existing Fund
    val id: Long? = null,

    // fill in abbreviation and description only when selecting/creating new Fund
    val abbreviation: String? = null,
    val description: String? = null

) {
    fun isCreation() = id == null
}

@InputProgrammeFundValidator
data class InputProgrammeFundWrapper(
    val funds: Collection<InputProgrammeFund>
)
