package io.cloudflight.ems.strategy.service

import io.cloudflight.ems.api.strategy.InputProgrammeStrategy
import io.cloudflight.ems.api.strategy.OutputProgrammeStrategy
import io.cloudflight.ems.strategy.entity.Strategy

fun Strategy.toStrategy() = OutputProgrammeStrategy(
    strategy = strategy,
    active = active
)

fun InputProgrammeStrategy.toEntity() = Strategy(
    strategy = strategy,
    active = active
)
