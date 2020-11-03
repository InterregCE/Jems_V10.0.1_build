package io.cloudflight.jems.server.programme.service.strategy

import io.cloudflight.jems.api.programme.dto.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.programme.dto.strategy.OutputProgrammeStrategy
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.repository.StrategyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StrategyServiceImpl(
    private val strategyRepository: StrategyRepository,
    private val auditService: AuditService
) : StrategyService {

    @Transactional(readOnly = true)
    override fun getProgrammeStrategies(): List<OutputProgrammeStrategy> {
        return strategyRepository.findAll().map { it.toStrategy() }
    }

    @Transactional
    override fun save(strategies: List<InputProgrammeStrategy>): List<OutputProgrammeStrategy> {
        strategyRepository.saveAll(strategies.map { it.toEntity() })

        val savedStrategies = strategyRepository.findAll().map { it.toStrategy() }
        strategyChanged(strategies = savedStrategies).logWith(auditService)
        return savedStrategies

    }
}
