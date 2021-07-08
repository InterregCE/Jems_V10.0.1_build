package io.cloudflight.jems.server.programme.service.legalstatus.update_legal_statuses

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.is_programme_setup_locked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.legalstatus.ProgrammeLegalStatusPersistence
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.cloudflight.jems.server.programme.service.programmeLegalStatusesChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateLegalStatus(
    private val persistence: ProgrammeLegalStatusPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher
) : UpdateLegalStatusInteractor {

    companion object {
        private const val MAX_ALLOWED_AMOUNT_OF_LEGAL_STATUSES = 20L
        private const val MAX_DESCRIPTION_LENGTH = 50
    }

    @CanUpdateProgrammeSetup
    @Transactional
    @ExceptionWrapper(UpdateLegalStatusesFailedException::class)
    override fun updateLegalStatuses(
        toDeleteIds: Set<Long>,
        toPersist: Collection<ProgrammeLegalStatus>
    ): List<ProgrammeLegalStatus> {

        validateInput(toPersist)

        if (toPersist.any { it.id == 0L && it.type != ProgrammeLegalStatusType.OTHER })
            throw CreationOfLegalStatusUnderPredefinedTypesIsNotAllowedException()

        if (isProgrammeSetupLocked.isLocked() && toDeleteIds.isNotEmpty())
            throw DeletionIsNotAllowedException()

        val readOnlyLegalStatuses =
            persistence.getByType(listOf(ProgrammeLegalStatusType.PRIVATE, ProgrammeLegalStatusType.PUBLIC))

        if (toDeleteIds.intersect(readOnlyLegalStatuses.map { it.id }).isNotEmpty())
            throw PredefinedLegalStatusesCannotBeDeletedException()

        // todo throw exception if description translation for the fallback language is changed (in MP2-1304 adding mandatory field checks)

        return persistence.updateLegalStatuses(toDeleteIds, toPersist).also { legalStatuses ->
            if (legalStatuses.size > MAX_ALLOWED_AMOUNT_OF_LEGAL_STATUSES)
                throw MaxAllowedLegalStatusesReachedException(MAX_ALLOWED_AMOUNT_OF_LEGAL_STATUSES)
            auditPublisher.publishEvent(programmeLegalStatusesChanged(this, legalStatuses))
        }

    }

    private fun validateInput(legalStatuses: Collection<ProgrammeLegalStatus>) =
        generalValidator.throwIfAnyIsInvalid(
            *legalStatuses.map { legalStatus ->
                generalValidator.maxLength(legalStatus.description, MAX_DESCRIPTION_LENGTH, "description")
            }.toTypedArray()
        )
}
