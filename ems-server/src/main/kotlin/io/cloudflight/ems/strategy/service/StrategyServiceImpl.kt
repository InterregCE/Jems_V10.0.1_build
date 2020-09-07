package io.cloudflight.ems.strategy.service

import io.cloudflight.ems.api.strategy.InputProgrammeStrategy
import io.cloudflight.ems.api.strategy.OutputProgrammeStrategy
import io.cloudflight.ems.audit.service.AuditService
import io.cloudflight.ems.strategy.repository.StrategyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StrategyServiceImpl (private val strategyRepository: StrategyRepository,
                           private val auditService: AuditService
) : StrategyService {

    @Transactional(readOnly = true)
    override fun getProgrammeStrategies(): List<OutputProgrammeStrategy> {
        return strategyRepository.findAll().map{ it.toStrategy() }
    }

    @Transactional
    override fun save(strategies: List<InputProgrammeStrategy>): List<OutputProgrammeStrategy> {
        strategyRepository.saveAll(strategies.map { it.toEntity() })

        val savedStrategies = strategyRepository.findAll().map{ it.toStrategy() }
        strategyChanged(strategies = savedStrategies).logWith(auditService)
        return savedStrategies

    }
}
