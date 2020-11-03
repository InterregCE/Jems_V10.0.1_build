package io.cloudflight.jems.server.programme.service.strategy

import io.cloudflight.jems.api.programme.dto.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy

interface StrategyService {
    fun getProgrammeStrategies(): List<OutputProgrammeStrategy>

    fun save(strategies: List<InputProgrammeStrategy>): List<OutputProgrammeStrategy>
}
