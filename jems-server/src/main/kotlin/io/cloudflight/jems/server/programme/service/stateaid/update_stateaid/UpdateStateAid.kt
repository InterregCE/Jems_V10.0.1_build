package io.cloudflight.jems.server.programme.service.stateaid.update_stateaid

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.programmeStateAidsChanged
import io.cloudflight.jems.server.programme.service.stateaid.ProgrammeStateAidPersistence
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

private const val MAX_ALLOWED_AMOUNT_OF_STATE_AIDS = 20

@Service
class UpdateStateAid(
    private val persistence: ProgrammeStateAidPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher
) : UpdateStateAidInteractor {

    @CanUpdateProgrammeSetup
    @Transactional
    @ExceptionWrapper(UpdateStateAidsFailedException::class)
    override fun updateStateAids(
        toDeleteIds: Set<Long>, toPersist: Collection<ProgrammeStateAid>
    ): List<ProgrammeStateAid> {
        validateInput(toPersist)
        validateAllowedChangesWhenCallIsPublished(toDeleteIds, toPersist)

        return persistence.updateStateAids(toDeleteIds, toPersist).also { stateAids ->
            if (stateAids.size > MAX_ALLOWED_AMOUNT_OF_STATE_AIDS)
                throw MaxAllowedStateAidsReachedException(MAX_ALLOWED_AMOUNT_OF_STATE_AIDS)
            auditPublisher.publishEvent(programmeStateAidsChanged(this, stateAids))
        }
    }

    private fun validateInput(stateAids: Collection<ProgrammeStateAid>) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxSize(stateAids, 20, "stateAids"),
            *stateAids.map {
                generalValidator.maxLength(it.name, 250, "name")
            }.toTypedArray(),
            *stateAids.map {
                generalValidator.maxLength(it.abbreviatedName, 50, "abbreviatedName")
            }.toTypedArray(),
            *stateAids.map {
                generalValidator.maxLength(it.schemeNumber, 25, "schemeNumber")
            }.toTypedArray(),
            *stateAids.map {
                generalValidator.maxLength(it.comments, 500, "comments")
            }.toTypedArray(),
            *stateAids.map {
                generalValidator.numberBetween(it.maxIntensity, BigDecimal(0), BigDecimal(100), "maxIntensity")
            }.toTypedArray()
        )

    private fun validateAllowedChangesWhenCallIsPublished(toDeleteIds: Set<Long>, toPersist: Collection<ProgrammeStateAid>) {
        if (!isProgrammeSetupLocked.isLocked())
            return

        if (toDeleteIds.isNotEmpty())
            throw DeletionIsNotAllowedException()

        val existingStateAidMap = persistence.getStateAidList().associateBy({ it.id }, { it.measure })
        val existingStateAidIds = existingStateAidMap.keys

        val toUpdateStateAidMap = toPersist
            .filter { existingStateAidIds.contains(it.id) }
            .associateBy({ it.id }, { it.measure })

        if (existingStateAidMap != toUpdateStateAidMap)
            throw MeasureChangeIsNotAllowed()
    }

}
