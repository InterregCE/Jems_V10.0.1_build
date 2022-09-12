package io.cloudflight.jems.server.programme.service.typologyerrors

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.programmeTypologyErrorsChanged
import io.cloudflight.jems.server.programme.service.typologyerrors.exception.DeletionIsNotAllowedException
import io.cloudflight.jems.server.programme.service.typologyerrors.exception.MaxAllowedTypologyErrorsReachedException
import io.cloudflight.jems.server.programme.service.typologyerrors.exception.UpdateTypologyErrorsFailedException
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateTypologyErrors(
    private val persistence: ProgrammeTypologyErrorsPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher
) : UpdateTypologyErrorsInteractor {

    companion object {
        private const val MAX_ALLOWED_AMOUNT_OF_TYPOLOGY_ERRORS = 50
        private const val MAX_DESCRIPTION_LENGTH = 500
    }

    @CanUpdateProgrammeSetup
    @Transactional
    @ExceptionWrapper(UpdateTypologyErrorsFailedException::class)
    override fun updateTypologyErrors(toDeleteIds: List<Long>, toPersist: List<TypologyErrors>): List<TypologyErrors> {
        validateInput(toPersist)
        if (toPersist.size > MAX_ALLOWED_AMOUNT_OF_TYPOLOGY_ERRORS)
            throw MaxAllowedTypologyErrorsReachedException(MAX_ALLOWED_AMOUNT_OF_TYPOLOGY_ERRORS)
        if (isProgrammeSetupLocked.isLocked() && toDeleteIds.isNotEmpty())
            throw DeletionIsNotAllowedException()

        return persistence.updateTypologyErrors(toDeleteIds, toPersist).also { typologyErrors ->
            auditPublisher.publishEvent(programmeTypologyErrorsChanged(this, typologyErrors))
        }
    }

    private fun validateInput(typologyErrorsList: Collection<TypologyErrors>) =
        generalValidator.throwIfAnyIsInvalid(
            *typologyErrorsList.map { typologyErrors ->
                generalValidator.maxLength(typologyErrors.description, MAX_DESCRIPTION_LENGTH, "description")
            }.toTypedArray()
        )
}
