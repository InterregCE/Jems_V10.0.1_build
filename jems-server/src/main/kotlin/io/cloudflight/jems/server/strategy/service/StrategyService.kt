package io.cloudflight.jems.server.strategy.service

import io.cloudflight.jems.api.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.strategy.OutputProgrammeStrategy

interface StrategyService {
    fun getProgrammeStrategies(): List<OutputProgrammeStrategy>

    fun save(strategies: List<InputProgrammeStrategy>): List<OutputProgrammeStrategy>
}
