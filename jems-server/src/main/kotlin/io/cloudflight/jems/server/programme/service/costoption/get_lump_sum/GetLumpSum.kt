package io.cloudflight.jems.server.programme.service.costoption.get_lump_sum

import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetLumpSum(
    private val persistence: ProgrammeLumpSumPersistence,
) : GetLumpSumInteractor {

    @CanUpdateProgrammeSetup
    @Transactional(readOnly = true)
    override fun getLumpSums(pageable: Pageable): Page<ProgrammeLumpSum> =
        persistence.getLumpSums(pageable)

    @CanUpdateProgrammeSetup
    @Transactional(readOnly = true)
    override fun getLumpSum(lumpSumId: Long): ProgrammeLumpSum =
        persistence.getLumpSum(lumpSumId)

}
