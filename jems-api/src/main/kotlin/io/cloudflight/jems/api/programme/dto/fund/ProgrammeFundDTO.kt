package io.cloudflight.jems.api.programme.dto.fund

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProgrammeFundDTO(
    val id: Long? = null,
    val selected: Boolean,
    val type: ProgrammeFundTypeDTO = ProgrammeFundTypeDTO.OTHER,
    val abbreviation: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet(),
) {
    fun isCreation() = id == null
}
