package io.cloudflight.jems.api.project.dto.partner.budget

import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class InputFlatRate(
    @field:Max(15)
    @field:Min(1)
    val value: Int?
)
