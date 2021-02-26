package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.server.programme.service.ProgrammePersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum

interface ProgrammeLumpSumPersistence: ProgrammePersistence {

    fun getLumpSums(): List<ProgrammeLumpSum>
    fun getLumpSum(lumpSumId: Long): ProgrammeLumpSum
    fun getCount(): Long
    fun createLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum
    fun updateLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum
    fun deleteLumpSum(lumpSumId: Long)

}
