package io.cloudflight.jems.server.programme.service.indicator.create_result_indicator

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.indicatorAdded
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

private const val MAX_COUNT_OF_RESULT_INDICATORS = 50

@Service
class CreateResultIndicator(
    private val persistence: ResultIndicatorPersistence,
    private val generalValidator: GeneralValidatorService,
    private val auditService: AuditService
) : CreateResultIndicatorInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(CreateResultIndicatorException::class)
    override fun createResultIndicator(resultIndicator: ResultIndicator): ResultIndicatorDetail {

        validateInput(resultIndicator)

        validateResultIndicatorDetail(resultIndicator)

        return persistence.saveResultIndicator(resultIndicator).apply {
            auditService.logEvent(indicatorAdded(resultIndicator.identifier))
        }
    }

    private fun validateResultIndicatorDetail(resultIndicator: ResultIndicator) {

        if (persistence.isIdentifierUsedByAnotherResultIndicator(resultIndicator.id, resultIndicator.identifier))
            throw IdentifierIsUsedException()

        if (persistence.getCountOfResultIndicators() >= MAX_COUNT_OF_RESULT_INDICATORS)
            throw ResultIndicatorsCountExceedException(MAX_COUNT_OF_RESULT_INDICATORS)
    }

    private fun validateInput(resultIndicator: ResultIndicator) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notNullOrZero(resultIndicator.id, "id"),
            generalValidator.notBlank(resultIndicator.identifier, "identifier"),
            generalValidator.maxLength(resultIndicator.identifier, 5, "identifier"),
            generalValidator.maxLength(resultIndicator.code, 6, "indicatorCode"),
            generalValidator.maxLength(resultIndicator.name, 255, "indicatorName"),
            generalValidator.maxLength(resultIndicator.measurementUnit, 255, "measurementUnit"),
            generalValidator.maxLength(resultIndicator.referenceYear, 10, "referenceYear"),
            generalValidator.maxLength(resultIndicator.sourceOfData, 1000, "sourceOfData"),
            generalValidator.maxLength(resultIndicator.comment, 1000, "comment"),
            generalValidator.minDecimal(resultIndicator.baseline, BigDecimal.ZERO, "baseline"),
            generalValidator.digits(resultIndicator.baseline, 9, 2, "baseline"),
            generalValidator.minDecimal(resultIndicator.finalTarget, BigDecimal.ZERO, "finalTarget"),
            generalValidator.digits(resultIndicator.finalTarget, 9, 2, "finalTarget")
        )
}
