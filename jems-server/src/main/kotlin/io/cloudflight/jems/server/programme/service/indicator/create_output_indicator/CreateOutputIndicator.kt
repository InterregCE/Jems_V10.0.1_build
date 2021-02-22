package io.cloudflight.jems.server.programme.service.indicator.create_output_indicator

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.indicatorAdded
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private const val MAX_NUMBER_OF_OUTPUT_INDICATORS = 50

@Service
class CreateOutputIndicator(
    private val persistence: OutputIndicatorPersistence,
    private val resultIndicatorPersistence: ResultIndicatorPersistence,
    private val auditService: AuditService
) : CreateOutputIndicatorInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(CreateOutputIndicatorException::class)
    override fun createOutputIndicator(outputIndicator: OutputIndicator): OutputIndicatorDetail {

        validateOutputIndicatorDetail(outputIndicator)

        return persistence.saveOutputIndicator(outputIndicator).also {
            auditService.logEvent(indicatorAdded(outputIndicator.identifier))
        }
    }

    private fun validateOutputIndicatorDetail(outputIndicator: OutputIndicator) {
        if (outputIndicator.id != 0L && outputIndicator.id != null)
            throw InvalidIdException()

        if (persistence.isIdentifierUsedByAnotherOutputIndicator(outputIndicator.id, outputIndicator.identifier))
            throw IdentifierIsUsedException()

        if (outputIndicator.resultIndicatorId != null &&
            resultIndicatorPersistence.getResultIndicator(outputIndicator.resultIndicatorId).programmeObjectivePolicy != outputIndicator.programmeObjectivePolicy
        ) throw InvalidResultIndicatorException()

        if (persistence.getCountOfOutputIndicators() >= MAX_NUMBER_OF_OUTPUT_INDICATORS)
            throw OutputIndicatorsCountExceedException()
    }
}
