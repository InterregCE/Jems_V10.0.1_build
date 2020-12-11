package io.cloudflight.jems.api.programme.dto.costoption

import java.math.BigDecimal

data class ProgrammeLumpSumListDTO(
    val id: Long? = null,
    val name: String? = null,
    val cost: BigDecimal? = null,
)
