package io.cloudflight.jems.server.programme.service.fund.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProgrammeFund(
    val id: Long = 0,
    val selected: Boolean,
    val type: ProgrammeFundType = ProgrammeFundType.OTHER,
    val abbreviation: Set<InputTranslation> = emptySet(),
    val description: Set<InputTranslation> = emptySet()
)
