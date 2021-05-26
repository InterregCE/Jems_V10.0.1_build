package io.cloudflight.jems.server.programme.service.costoption.get_lump_sum

import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetLumpSum(
    private val persistence: ProgrammeLumpSumPersistence,
) : GetLumpSumInteractor {

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    override fun getLumpSums(): List<ProgrammeLumpSum> =
        persistence.getLumpSums()

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    override fun getLumpSum(lumpSumId: Long): ProgrammeLumpSum =
        persistence.getLumpSum(lumpSumId)

}
