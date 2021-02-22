package io.cloudflight.jems.server.programme.service.indicator.update_result_indicator

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.indicatorEdited
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateResultIndicator(
    private val persistence: ResultIndicatorPersistence,
    private val auditService: AuditService
) : UpdateResultIndicatorInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(UpdateResultIndicatorException::class)
    override fun updateResultIndicator(resultIndicator: ResultIndicator): ResultIndicatorDetail {

        validateResultIndicator(resultIndicator)

        val oldResultIndicator = persistence.getResultIndicator(resultIndicator.id!!)
        val savedResultIndicator = persistence.saveResultIndicator(resultIndicator)

        auditService.logEvent(
            indicatorEdited(
                identifier = savedResultIndicator.identifier,
                changes = oldResultIndicator.getDiff(savedResultIndicator)
            )
        )
        return savedResultIndicator
    }


    private fun validateResultIndicator(resultIndicator: ResultIndicator) {
        if (resultIndicator.id == null || resultIndicator.id == 0L)
            throw InvalidIdException()

        if (persistence.isIdentifierUsedByAnotherResultIndicator(resultIndicator.id, resultIndicator.identifier))
            throw IdentifierIsUsedException()

    }

}
