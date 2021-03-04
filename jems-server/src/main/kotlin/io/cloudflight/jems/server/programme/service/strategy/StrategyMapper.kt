package io.cloudflight.jems.server.programme.service.strategy

import io.cloudflight.jems.api.programme.dto.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity

fun ProgrammeStrategyEntity.toStrategy() = OutputProgrammeStrategy(
    strategy = strategy,
    active = active
)

fun InputProgrammeStrategy.toEntity() = ProgrammeStrategyEntity(
    strategy = strategy,
    active = active
)
