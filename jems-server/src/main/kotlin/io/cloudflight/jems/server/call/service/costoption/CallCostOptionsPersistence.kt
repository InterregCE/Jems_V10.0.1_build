package io.cloudflight.jems.server.call.service.costoption

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

interface CallCostOptionsPersistence {

    fun updateProjectCallLumpSum(callId: Long, lumpSumIds: Set<Long>)

    fun getProjectCallLumpSum(callId: Long): Iterable<ProgrammeLumpSum>

    fun updateProjectCallUnitCost(callId: Long, unitCostIds: Set<Long>)

    fun getProjectCallUnitCost(callId: Long): Iterable<ProgrammeUnitCost>

}
