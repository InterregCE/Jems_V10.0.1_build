package io.cloudflight.jems.server.strategy.service

import io.cloudflight.jems.api.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.strategy.OutputProgrammeStrategy
import io.cloudflight.jems.server.strategy.entity.Strategy

fun Strategy.toStrategy() = OutputProgrammeStrategy(
    strategy = strategy,
    active = active
)

fun InputProgrammeStrategy.toEntity() = Strategy(
    strategy = strategy,
    active = active
)
