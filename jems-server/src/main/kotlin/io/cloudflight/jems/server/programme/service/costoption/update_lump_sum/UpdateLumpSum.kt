package io.cloudflight.jems.server.programme.service.costoption.update_lump_sum

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.validateUpdateLumpSum
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.lumpSumChangedAudit
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateLumpSum(
    private val persistence: ProgrammeLumpSumPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val auditPublisher: ApplicationEventPublisher,
    private val generalValidator: GeneralValidatorService,
) : UpdateLumpSumInteractor {

    @CanUpdateProgrammeSetup
    @Transactional
    @ExceptionWrapper(UpdateLumpSumException::class)
    override fun updateLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum {
        validateInput(lumpSum)

        validateUpdateLumpSum(lumpSum)

        val existingLumpSum  = persistence.getLumpSum(lumpSumId = lumpSum.id)
        if (isProgrammeSetupLocked.isLocked()) {
            lumpSumUpdateRestrictions(existingLumpSum = existingLumpSum, updatedLumpSum = lumpSum)
        }
        if (isProgrammeSetupLocked.isAnyReportCreated()) {
            fastTrackLumpSumUpdateRestrictionsAfterReportCreation(existingLumpSum = existingLumpSum, updatedLumpSum = lumpSum)
        }
        if (isProgrammeSetupLocked.isFastTrackLumpSumReadyForPayment(lumpSum.id)) {
            fastTrackLumpSumUpdateRestrictionsAfterReadyForPayment(lumpSum)
        }

        return persistence.updateLumpSum(lumpSum).also {
            auditPublisher.publishEvent(lumpSumChangedAudit(this, it, existingLumpSum))
        }
    }

    private fun lumpSumUpdateRestrictions(existingLumpSum: ProgrammeLumpSum, updatedLumpSum: ProgrammeLumpSum) {
        if (existingLumpSum.cost?.compareTo(updatedLumpSum.cost) != 0 ||
            existingLumpSum.splittingAllowed != updatedLumpSum.splittingAllowed ||
            existingLumpSum.phase != updatedLumpSum.phase ||
            existingLumpSum.categories != updatedLumpSum.categories
        )
            throw UpdateLumpSumWhenProgrammeSetupRestricted()
    }

    private fun fastTrackLumpSumUpdateRestrictionsAfterReportCreation(
        existingLumpSum: ProgrammeLumpSum,
        updatedLumpSum: ProgrammeLumpSum
    ) {
        if (existingLumpSum.fastTrack != updatedLumpSum.fastTrack)
            throw UpdateLumpSumWhenProgrammeSetupRestricted()
    }

    private fun fastTrackLumpSumUpdateRestrictionsAfterReadyForPayment(updatedLumpSum: ProgrammeLumpSum) {
        if (!updatedLumpSum.fastTrack)
            throw UpdateLumpSumWhenProgrammeSetupRestricted()
    }

    private fun validateInput(programmeLumpSum: ProgrammeLumpSum) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notNullOrZero(programmeLumpSum.id, "id"),
            generalValidator.maxLength(programmeLumpSum.name, 50, "name"),
            generalValidator.maxLength(programmeLumpSum.description, 255, "description"),
            generalValidator.notNull(programmeLumpSum.phase, "phase"),
    )

}
