package io.cloudflight.jems.server.strategy.service

import io.cloudflight.jems.api.programme.dto.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.entity.Strategy
import io.cloudflight.jems.server.programme.repository.StrategyRepository
import io.cloudflight.jems.server.programme.service.strategy.StrategyService
import io.cloudflight.jems.server.programme.service.strategy.StrategyServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StrategyServiceTest {
    companion object {
        val strategy = Strategy(
            strategy = ProgrammeStrategy.EUStrategyAdriaticIonianRegion,
            active = false
        )
    }
    @MockK
    lateinit var strategyRepository: StrategyRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    lateinit var strategyService: StrategyService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        strategyService = StrategyServiceImpl(
            strategyRepository,
            auditService
        )
    }

    @Test
    fun get() {
        every { strategyRepository.findAll() } returns listOf(strategy)
        assertThat(strategyService.getProgrammeStrategies()).isEqualTo(listOf(OutputProgrammeStrategy(strategy = strategy.strategy, active = strategy.active)))
    }

    @Test
    fun `update existing - active`() {
        every { strategyRepository.saveAll(any<List<Strategy>>()) } returnsArgument 0
        val expectedResult = listOf(strategy.copy(active = true))
        every { strategyRepository.count() } returns expectedResult.size.toLong()
        every { strategyRepository.findAll() } returns expectedResult

        val existingFundToSelect = InputProgrammeStrategy(strategy = ProgrammeStrategy.EUStrategyAdriaticIonianRegion, active = true)
        val result = strategyService.save(listOf(existingFundToSelect))
        assertThat(result).isEqualTo(listOf(OutputProgrammeStrategy(strategy = ProgrammeStrategy.EUStrategyAdriaticIonianRegion, active = true)))

        val audit = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(audit)) }
        with(audit) {
            assertThat(captured.action).isEqualTo(AuditAction.PROGRAMME_STRATEGIES_CHANGED)
            assertThat(captured.description).isEqualTo("Programme strategies was set to:\nEUStrategyAdriaticIonianRegion")
        }
    }
}
