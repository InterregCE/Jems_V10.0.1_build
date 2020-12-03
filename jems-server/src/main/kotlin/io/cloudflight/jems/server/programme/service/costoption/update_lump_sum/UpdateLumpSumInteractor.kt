package io.cloudflight.jems.server.programme.service.costoption.update_lump_sum

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum

interface UpdateLumpSumInteractor {

    fun updateLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum

}
