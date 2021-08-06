package io.cloudflight.jems.server.programme.service.stateaid.update_stateaid

import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.is_programme_setup_locked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.legalstatus.update_legal_statuses.DeletionIsNotAllowedException
import io.cloudflight.jems.server.programme.service.programmeStateAidsChanged
import io.cloudflight.jems.server.programme.service.stateaid.ProgrammeStateAidPersistence
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.Exception

@Service
class UpdateStateAid(
    private val persistence: ProgrammeStateAidPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher
) : UpdateStateAidInteractor {

    companion object {
        private const val MAX_ALLOWED_AMOUNT_OF_STATE_AIDS = 20
        private const val MAX_NAME_LENGTH = 150
        private const val MAX_ABBREVIATED_NAME_LENGTH = 50
        private const val MAX_SCHEME_NUMBER_LENGTH = 25
        private const val MAX_COMMENTS_LENGTH = 500
        private const val MIN_INTENSITY_VALUE = 0
        private const val MAX_INTENSITY_VALUE = 100
    }

    @CanUpdateProgrammeSetup
    @Transactional
    override fun updateStateAids(
        toDeleteIds: Set<Long>,
        toPersist: Collection<ProgrammeStateAid>
    ): List<ProgrammeStateAid> {

        validateInput(toPersist)

        if (isProgrammeSetupLocked.isLocked() && toDeleteIds.isNotEmpty())
            throw DeletionIsNotAllowedException()

        return persistence.updateStateAids(toDeleteIds, toPersist).also { stateAids ->
            if (stateAids.size > MAX_ALLOWED_AMOUNT_OF_STATE_AIDS)
                throw Exception("TODO")
            auditPublisher.publishEvent(programmeStateAidsChanged(this, stateAids))
        }
    }

    private fun validateInput(stateAids: Collection<ProgrammeStateAid>) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxSize(stateAids, MAX_ALLOWED_AMOUNT_OF_STATE_AIDS, "stateAids"),
            *stateAids.map {
                generalValidator.maxLength(it.name, MAX_NAME_LENGTH, "name")
            }.toTypedArray(),
            *stateAids.map {
                generalValidator.maxLength(it.abbreviatedName, MAX_ABBREVIATED_NAME_LENGTH, "abbreviatedName")
            }.toTypedArray(),
            *stateAids.map {
                generalValidator.maxLength(it.schemeNumber, MAX_SCHEME_NUMBER_LENGTH, "schemeNumber")
            }.toTypedArray(),
            *stateAids.map {
                generalValidator.maxLength(it.comments, MAX_COMMENTS_LENGTH, "comments")
            }.toTypedArray(),
            *stateAids.map {
                generalValidator.numberBetween(it.maxIntensity?.toInt(), MIN_INTENSITY_VALUE, MAX_INTENSITY_VALUE, "maxIntensity")
            }.toTypedArray()
        )

}
