package io.cloudflight.jems.server.programme.service.strategy

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.programme.authorization.CanReadProgrammeSetup
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.repository.StrategyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StrategyServiceImpl(
    private val strategyRepository: StrategyRepository,
    private val callRepository: CallRepository,
    private val auditService: AuditService
) : StrategyService {

    @CanReadProgrammeSetup
    @Transactional(readOnly = true)
    override fun getProgrammeStrategies(): List<OutputProgrammeStrategy> {
        return strategyRepository.findAll().map { it.toStrategy() }
    }

    @CanUpdateProgrammeSetup
    @Transactional
    override fun save(strategies: List<InputProgrammeStrategy>): List<OutputProgrammeStrategy> {
        val oldSelectedStrategies = strategyRepository.findAll().filter { it.active }.mapTo(HashSet()) { it.strategy }
        val toBeSaved = strategies.mapTo(HashSet()) { it.toEntity() }
        val newSelectedStrategies = toBeSaved.filter { it.active }.mapTo(HashSet()) { it.strategy }

        if (callRepository.existsByStatus(CallStatus.PUBLISHED) && !newSelectedStrategies.containsAll(oldSelectedStrategies))
            throw UpdateStrategiesWhenProgrammeSetupRestricted()

        strategyRepository.saveAll(toBeSaved)

        val savedStrategies = toBeSaved.map { it.toStrategy() }
        strategyChanged(strategies = savedStrategies).logWith(auditService)
        return savedStrategies

    }
}
