package io.cloudflight.jems.server.programme.service.indicator.create_output_indicator

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.indicatorAdded
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

private const val MAX_COUNT_OF_OUTPUT_INDICATORS = 50

@Service
class CreateOutputIndicator(
    private val persistence: OutputIndicatorPersistence,
    private val resultIndicatorPersistence: ResultIndicatorPersistence,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher,
) : CreateOutputIndicatorInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(CreateOutputIndicatorException::class)
    override fun createOutputIndicator(outputIndicator: OutputIndicator): OutputIndicatorDetail {

        validateInput(outputIndicator)

        validateOutputIndicatorDetail(outputIndicator)

        return persistence.saveOutputIndicator(outputIndicator).also {
            auditPublisher.publishEvent(indicatorAdded(this, outputIndicator.identifier))
        }
    }

    private fun validateOutputIndicatorDetail(outputIndicator: OutputIndicator) {

        if (persistence.isIdentifierUsedByAnotherOutputIndicator(outputIndicator.id, outputIndicator.identifier))
            throw IdentifierIsUsedException()

        if (outputIndicator.resultIndicatorId != null && outputIndicator.resultIndicatorId != 0L &&
            resultIndicatorPersistence.getResultIndicator(outputIndicator.resultIndicatorId).programmeObjectivePolicy != outputIndicator.programmeObjectivePolicy
        ) throw InvalidResultIndicatorException()

        if (persistence.getCountOfOutputIndicators() >= MAX_COUNT_OF_OUTPUT_INDICATORS)
            throw OutputIndicatorsCountExceedException(MAX_COUNT_OF_OUTPUT_INDICATORS)
    }

    private fun validateInput(outputIndicator: OutputIndicator) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.nullOrZero(outputIndicator.id, "id"),
            generalValidator.notBlank(outputIndicator.identifier, "identifier"),
            generalValidator.maxLength(outputIndicator.identifier, 5, "identifier"),
            generalValidator.maxLength(outputIndicator.code, 6, "indicatorCode"),
            generalValidator.maxLength(outputIndicator.name, 255, "indicatorName"),
            generalValidator.maxLength(outputIndicator.measurementUnit, 255, "measurementUnit"),
            generalValidator.minDecimal(outputIndicator.milestone, BigDecimal.ZERO, "milestone"),
            generalValidator.digits(outputIndicator.milestone, 9, 2, "milestone"),
            generalValidator.minDecimal(outputIndicator.finalTarget, BigDecimal.ZERO, "finalTarget"),
            generalValidator.digits(outputIndicator.finalTarget, 9, 2, "finalTarget")
        )
}
