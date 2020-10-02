package io.cloudflight.ems.strategy.service

import io.cloudflight.ems.api.strategy.InputProgrammeStrategy
import io.cloudflight.ems.api.strategy.OutputProgrammeStrategy
import io.cloudflight.ems.api.strategy.ProgrammeStrategy
import io.cloudflight.ems.audit.entity.AuditAction
import io.cloudflight.ems.audit.service.AuditCandidate
import io.cloudflight.ems.audit.service.AuditService
import io.cloudflight.ems.strategy.entity.Strategy
import io.cloudflight.ems.strategy.repository.StrategyRepository
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
