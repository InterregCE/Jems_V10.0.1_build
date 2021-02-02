package io.cloudflight.jems.server.programme.service.costoption.get_lump_sum

import io.cloudflight.jems.server.programme.authorization.CanReadProgrammeSetup
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetLumpSum(
    private val persistence: ProgrammeLumpSumPersistence,
) : GetLumpSumInteractor {

    @CanReadProgrammeSetup
    @Transactional(readOnly = true)
    override fun getLumpSums(): List<ProgrammeLumpSum> =
        persistence.getLumpSums()

    @CanUpdateProgrammeSetup
    @Transactional(readOnly = true)
    override fun getLumpSum(lumpSumId: Long): ProgrammeLumpSum =
        persistence.getLumpSum(lumpSumId)

}
