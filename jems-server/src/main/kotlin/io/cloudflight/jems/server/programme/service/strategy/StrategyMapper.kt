package io.cloudflight.jems.server.programme.service.strategy

import io.cloudflight.jems.api.programme.dto.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity

fun Iterable<ProgrammeStrategyEntity>.toDto() = map {
    OutputProgrammeStrategy(
        strategy = it.strategy,
        active = it.active
    )
}.sortedBy { it.strategy }

fun InputProgrammeStrategy.toEntity() = ProgrammeStrategyEntity(
    strategy = strategy,
    active = active
)
