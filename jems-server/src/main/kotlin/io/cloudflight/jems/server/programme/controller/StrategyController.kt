package io.cloudflight.jems.server.programme.controller

import io.cloudflight.jems.api.programme.dto.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy
import io.cloudflight.jems.api.programme.ProgrammeStrategyApi
import io.cloudflight.jems.server.programme.service.strategy.StrategyService
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
