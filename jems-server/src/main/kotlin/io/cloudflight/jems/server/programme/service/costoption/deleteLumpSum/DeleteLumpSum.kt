package io.cloudflight.jems.server.programme.service.costoption.deleteLumpSum

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.lumpSumDeleted
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteLumpSum(
    private val persistence: ProgrammeLumpSumPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val auditPublisher: ApplicationEventPublisher
) : DeleteLumpSumInteractor {

    @CanUpdateProgrammeSetup
    @Transactional
    @ExceptionWrapper(DeleteLumpSumFailed::class)
    override fun deleteLumpSum(lumpSumId: Long) {
        if (isProgrammeSetupLocked.isLocked())
            throw DeleteLumpSumWhenProgrammeSetupRestricted()

        val lumpSumsUsedInCall = persistence.getNumberOfOccurrencesInCalls(lumpSumId)
        if (lumpSumsUsedInCall > 0){
            throw ToDeleteLumpSumAlreadyUsedInCall()
        }
        val lumpSumToBeDeleted = persistence.getLumpSum(lumpSumId)
        persistence.deleteLumpSum(lumpSumId).also{
            auditPublisher.publishEvent(lumpSumDeleted(this, lumpSumToBeDeleted))
        }
    }
}
