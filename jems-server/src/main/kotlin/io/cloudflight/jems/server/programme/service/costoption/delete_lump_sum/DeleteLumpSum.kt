package io.cloudflight.jems.server.programme.service.costoption.delete_lump_sum

import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.is_programme_setup_locked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.costoption.DeleteLumpSumWhenProgrammeSetupRestricted
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteLumpSum(
    private val persistence: ProgrammeLumpSumPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
) : DeleteLumpSumInteractor {

    @CanUpdateProgrammeSetup
    @Transactional(readOnly = true)
    override fun deleteLumpSum(lumpSumId: Long) {
        if (isProgrammeSetupLocked.isLocked())
            throw DeleteLumpSumWhenProgrammeSetupRestricted()
        persistence.deleteLumpSum(lumpSumId)
    }

}
