package io.cloudflight.jems.server.programme.service.costoption.create_lump_sum

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum

interface CreateLumpSumInteractor {

    fun createLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum

}
