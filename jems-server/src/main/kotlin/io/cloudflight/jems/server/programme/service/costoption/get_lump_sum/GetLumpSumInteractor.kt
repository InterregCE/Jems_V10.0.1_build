package io.cloudflight.jems.server.programme.service.costoption.get_lump_sum

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum

interface GetLumpSumInteractor {

    fun getLumpSums(): List<ProgrammeLumpSum>

    fun getLumpSum(lumpSumId: Long): ProgrammeLumpSum

}
