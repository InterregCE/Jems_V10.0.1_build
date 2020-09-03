package io.cloudflight.ems.strategy.controller

import io.cloudflight.ems.api.strategy.InputProgrammeStrategy
import io.cloudflight.ems.api.strategy.OutputProgrammeStrategy
import io.cloudflight.ems.api.strategy.ProgrammeStrategyApi
import io.cloudflight.ems.strategy.service.StrategyService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("@programmeSetupAuthorization.canAccessSetup()")
class StrategyController(private val strategyService: StrategyService) : ProgrammeStrategyApi {
    override fun getProgrammeStrategies(): List<OutputProgrammeStrategy> {
        return strategyService.getProgrammeStrategies();
    }

    override fun updateProgrammeStrategies(strategies: List<InputProgrammeStrategy>): List<OutputProgrammeStrategy> {
        return strategyService.save(strategies);
    }
}
