package io.cloudflight.ems.strategy.service

import io.cloudflight.ems.api.strategy.InputProgrammeStrategy
import io.cloudflight.ems.api.strategy.OutputProgrammeStrategy

interface StrategyService {
    fun getProgrammeStrategies(): List<OutputProgrammeStrategy>

    fun save(strategies: List<InputProgrammeStrategy>): List<OutputProgrammeStrategy>
}
