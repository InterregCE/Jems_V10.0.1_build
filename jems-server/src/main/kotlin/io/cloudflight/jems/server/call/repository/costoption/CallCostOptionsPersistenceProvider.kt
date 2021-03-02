package io.cloudflight.jems.server.call.repository.costoption

import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.call.service.costoption.CallCostOptionsPersistence
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.costoption.toProgrammeLumpSum
import io.cloudflight.jems.server.programme.repository.costoption.toProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CallCostOptionsPersistenceProvider(
    private val callRepo: CallRepository,
    private val programmeLumpSumRepo: ProgrammeLumpSumRepository,
    private val programmeUnitCostRepo: ProgrammeUnitCostRepository,
) : CallCostOptionsPersistence {

    @Transactional
    override fun updateProjectCallLumpSum(callId: Long, lumpSumIds: Set<Long>) {
        val lumpSums = programmeLumpSumRepo.findAllById(lumpSumIds).toSet()
        if (lumpSums.size != lumpSumIds.size)
            throw ResourceNotFoundException("programmeLumpSum")
        callRepo.save(
            getCallOrThrow(callId).copy(lumpSums = lumpSums.toSet())
        )
    }

    @Transactional(readOnly = true)
    override fun getProjectCallLumpSum(callId: Long): Iterable<ProgrammeLumpSum> =
        getCallOrThrow(callId).lumpSums.map { it.toProgrammeLumpSum() }

    @Transactional
    override fun updateProjectCallUnitCost(callId: Long, unitCostIds: Set<Long>) {
        val unitCosts = programmeUnitCostRepo.findAllById(unitCostIds).toSet()
        if (unitCosts.size != unitCostIds.size)
            throw ResourceNotFoundException("programmeUnitCost")
        callRepo.save(
            getCallOrThrow(callId).copy(unitCosts = unitCosts.toSet())
        )
    }

    @Transactional(readOnly = true)
    override fun getProjectCallUnitCost(callId: Long): Iterable<ProgrammeUnitCost> =
        getCallOrThrow(callId).unitCosts.map { it.toProgrammeUnitCost() }

    private fun getCallOrThrow(callId: Long) =
        callRepo.findById(callId).orElseThrow { ResourceNotFoundException("call") }

}
