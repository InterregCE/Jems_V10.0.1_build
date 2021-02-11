package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.indicator.IndicatorResultDto
import io.cloudflight.jems.server.programme.service.indicator.IndicatorResultPersistence
import io.cloudflight.jems.server.programme.controller.indicator.toIndicatorResultDto
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class IndicatorResultPersistenceProvider(
    private val indicatorResultRepository: IndicatorResultRepository
) : IndicatorResultPersistence {

    @Transactional(readOnly = true)
    override fun getResultIndicatorsDetails(): Set<IndicatorResultDto> {
        return indicatorResultRepository.findAll().map { it.toIndicatorResultDto() }.toSet()
    }

    @Transactional(readOnly = true)
    override fun getResultIndicatorsForSpecificObjective(code: String): List<IndicatorResultDto> {
        return indicatorResultRepository.findAllByProgrammePriorityPolicyCodeOrderById(code)
            .map { it.toIndicatorResultDto() }
    }
}
