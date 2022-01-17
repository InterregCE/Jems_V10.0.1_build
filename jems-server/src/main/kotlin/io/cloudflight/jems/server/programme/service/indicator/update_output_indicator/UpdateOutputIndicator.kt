package io.cloudflight.jems.server.programme.service.indicator.update_output_indicator

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.indicatorEdited
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateOutputIndicator(
    private val persistence: OutputIndicatorPersistence,
    private val generalValidator: GeneralValidatorService,
    private val resultIndicatorPersistence: ResultIndicatorPersistence,
    private val callPersistence: CallPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : UpdateOutputIndicatorInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(UpdateOutputIndicatorException::class)
    override fun updateOutputIndicator(outputIndicator: OutputIndicator): OutputIndicatorDetail {
        validateInput(outputIndicator)
        validateOutputIndicatorDetail(outputIndicator)

        val oldOutputIndicator = persistence.getOutputIndicator(outputIndicator.id!!)

        checkUpdateConstraintsAfterFirstPublishedCall(outputIndicator, oldOutputIndicator)

        val savedOutputIndicator = persistence.saveOutputIndicator(outputIndicator)

        auditPublisher.publishEvent(
            indicatorEdited(
                context = this,
                identifier = savedOutputIndicator.identifier,
                changes = oldOutputIndicator.getDiff(savedOutputIndicator)
            )
        )
        return savedOutputIndicator
    }


    private fun validateOutputIndicatorDetail(outputIndicator: OutputIndicator) {
        if (persistence.isIdentifierUsedByAnotherOutputIndicator(outputIndicator.id, outputIndicator.identifier))
            throw IdentifierIsUsedException()

        if (outputIndicator.resultIndicatorId != null) {
            val resultIndicator = resultIndicatorPersistence.getResultIndicator(outputIndicator.resultIndicatorId)
            if (resultIndicator.programmeObjectivePolicy != outputIndicator.programmeObjectivePolicy)
                throw InvalidResultIndicatorException()
        }
    }

    private fun validateInput(outputIndicator: OutputIndicator) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notNullOrZero(outputIndicator.id, "id"),
            generalValidator.notBlank(outputIndicator.identifier, "identifier"),
            generalValidator.maxLength(outputIndicator.identifier, 10, "identifier"),
            generalValidator.maxLength(outputIndicator.code, 6, "indicatorCode"),
            generalValidator.maxLength(outputIndicator.name, 255, "indicatorName"),
            generalValidator.maxLength(outputIndicator.measurementUnit, 255, "measurementUnit"),
            generalValidator.minDecimal(outputIndicator.milestone, BigDecimal.ZERO, "milestone"),
            generalValidator.digits(outputIndicator.milestone, 9, 2, "milestone"),
            generalValidator.minDecimal(outputIndicator.finalTarget, BigDecimal.ZERO, "finalTarget"),
            generalValidator.digits(outputIndicator.finalTarget, 9, 2, "finalTarget")
        )

    private fun checkUpdateConstraintsAfterFirstPublishedCall(
        outputIndicator: OutputIndicator,
        oldOutputIndicatorDetail: OutputIndicatorDetail
    ) {
        if (!callPersistence.hasAnyCallPublished())
            return

        val changedLockedFields = HashSet<String>()

        if (oldOutputIndicatorDetail.programmeObjectivePolicy != outputIndicator.programmeObjectivePolicy)
            changedLockedFields.add("specificObjective")

        if (oldOutputIndicatorDetail.code != outputIndicator.code)
            changedLockedFields.add("indicatorCode")

        if (changedLockedFields.isNotEmpty())
            throw OutputIndicatorCannotBeChangedAfterCallIsPublished(changedLockedFields)
    }

}
