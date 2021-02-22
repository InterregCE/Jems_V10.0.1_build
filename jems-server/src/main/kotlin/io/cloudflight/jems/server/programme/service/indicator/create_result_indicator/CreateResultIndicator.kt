package io.cloudflight.jems.server.programme.service.indicator.create_result_indicator

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.indicatorAdded
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private const val MAX_COUNT_OF_RESULT_INDICATORS = 50

@Service
class CreateResultIndicator(
    private val persistence: ResultIndicatorPersistence,
    private val auditService: AuditService
) : CreateResultIndicatorInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(CreateResultIndicatorException::class)
    override fun createResultIndicator(resultIndicator: ResultIndicator): ResultIndicatorDetail {

        validateResultIndicatorDetail(resultIndicator)

        return persistence.saveResultIndicator(resultIndicator).apply {
            auditService.logEvent(indicatorAdded(resultIndicator.identifier))
        }
    }

    private fun validateResultIndicatorDetail(resultIndicator: ResultIndicator) {
        if (resultIndicator.id != 0L && resultIndicator.id != null)
            throw InvalidIdException()

        if (persistence.isIdentifierUsedByAnotherResultIndicator(resultIndicator.id, resultIndicator.identifier))
            throw IdentifierIsUsedException()

        if (persistence.getCountOfResultIndicators() >= MAX_COUNT_OF_RESULT_INDICATORS)
            throw ResultIndicatorsCountExceedException(MAX_COUNT_OF_RESULT_INDICATORS)
    }
}
