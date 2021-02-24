package io.cloudflight.jems.server.programme.service.indicator.update_output_indicator

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.indicatorEdited
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateOutputIndicator(
    private val persistence: OutputIndicatorPersistence,
    private val resultIndicatorPersistence: ResultIndicatorPersistence,
    private val auditService: AuditService
) : UpdateOutputIndicatorInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(UpdateOutputIndicatorException::class)
    override fun updateOutputIndicator(outputIndicator: OutputIndicator): OutputIndicatorDetail {

        validateOutputIndicatorDetail(outputIndicator)

        val oldOutputIndicator = persistence.getOutputIndicator(outputIndicator.id!!)
        val savedOutputIndicator = persistence.saveOutputIndicator(outputIndicator)

        auditService.logEvent(
            indicatorEdited(
                identifier = savedOutputIndicator.identifier,
                changes = oldOutputIndicator.getDiff(savedOutputIndicator)
            )
        )
        return savedOutputIndicator
    }


    private fun validateOutputIndicatorDetail(outputIndicator: OutputIndicator) {
        if (outputIndicator.id == null || outputIndicator.id == 0L)
            throw InvalidIdException()

        if (persistence.isIdentifierUsedByAnotherOutputIndicator(outputIndicator.id, outputIndicator.identifier))
            throw IdentifierIsUsedException()

        if (outputIndicator.resultIndicatorId != null && outputIndicator.resultIndicatorId != 0L &&
            resultIndicatorPersistence.getResultIndicator(outputIndicator.resultIndicatorId).programmeObjectivePolicy != outputIndicator.programmeObjectivePolicy
        ) throw InvalidResultIndicatorException()
    }

}
