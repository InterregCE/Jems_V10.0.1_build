package io.cloudflight.jems.server.programme.service.costoption.get_lump_sum

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetLumpSumInteractor {

    fun getLumpSums(pageable: Pageable): Page<ProgrammeLumpSum>

    fun getLumpSum(lumpSumId: Long): ProgrammeLumpSum

}
