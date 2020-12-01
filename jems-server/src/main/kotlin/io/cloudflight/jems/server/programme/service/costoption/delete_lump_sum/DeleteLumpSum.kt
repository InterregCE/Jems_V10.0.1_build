package io.cloudflight.jems.server.programme.service.costoption.delete_lump_sum

import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteLumpSum(
    private val persistence: ProgrammeLumpSumPersistence,
) : DeleteLumpSumInteractor {

    @CanUpdateProgrammeSetup
    @Transactional(readOnly = true)
    override fun deleteLumpSum(lumpSumId: Long) =
        persistence.deleteLumpSum(lumpSumId)

}
