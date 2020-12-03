package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProgrammeLumpSumPersistence {

    fun getLumpSums(pageable: Pageable): Page<ProgrammeLumpSum>
    fun getLumpSum(lumpSumId: Long): ProgrammeLumpSum
    fun createLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum
    fun updateLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum
    fun deleteLumpSum(lumpSumId: Long)

}
