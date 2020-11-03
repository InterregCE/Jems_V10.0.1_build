package io.cloudflight.jems.server.programme.service.strategy

import io.cloudflight.jems.api.programme.dto.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy
import io.cloudflight.jems.server.programme.entity.Strategy

fun Strategy.toStrategy() = OutputProgrammeStrategy(
    strategy = strategy,
    active = active
)

fun InputProgrammeStrategy.toEntity() = Strategy(
    strategy = strategy,
    active = active
)
