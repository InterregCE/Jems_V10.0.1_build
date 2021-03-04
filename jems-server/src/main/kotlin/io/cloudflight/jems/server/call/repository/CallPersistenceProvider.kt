package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.programme.repository.StrategyRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.priority.ProgrammeSpecificObjectiveRepository
import io.cloudflight.jems.server.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class CallPersistenceProvider(
    private val callRepo: CallRepository,
    private val userRepo: UserRepository,
    private val programmeLumpSumRepo: ProgrammeLumpSumRepository,
    private val programmeUnitCostRepo: ProgrammeUnitCostRepository,
    private val programmeSpecificObjectiveRepo: ProgrammeSpecificObjectiveRepository,
    private val programmeStrategyRepo: StrategyRepository,
    private val programmeFundRepo: ProgrammeFundRepository,
) : CallPersistence {

    @Transactional(readOnly = true)
    override fun getCalls(pageable: Pageable): Page<CallSummary> =
        callRepo.findAll(pageable).toModel()

    @Transactional(readOnly = true)
    override fun getPublishedAndOpenCalls(pageable: Pageable): Page<CallSummary> =
        callRepo.findAllByStatusAndEndDateAfter(CallStatus.PUBLISHED, ZonedDateTime.now(), pageable).toModel()

    @Transactional(readOnly = true)
    override fun getCallById(callId: Long): CallDetail =
        callRepo.findById(callId).map { it.toDetailModel() }.orElseThrow { CallNotFound() }

    @Transactional(readOnly = true)
    override fun getCallIdForNameIfExists(name: String): Long? =
        callRepo.findFirstByName(name)?.id

    @Transactional
    override fun createCall(call: Call, userId: Long): CallDetail =
        callRepo.save(
            call.toEntity(
                user = userRepo.getOne(userId),
                retrieveSpecificObjective = { programmeSpecificObjectiveRepo.getOne(it) },
                retrieveStrategies = { programmeStrategyRepo.getAllByStrategyInAndActiveTrue(it).toSet() },
                retrieveFunds = { programmeFundRepo.getTop20ByIdInAndSelectedTrue(it).toSet() },
            )
        ).toDetailModel()

    @Transactional
    override fun updateCall(call: Call): CallDetail {
        val existingCall = callRepo.findById(call.id).orElseThrow { CallNotFound() }

        return callRepo.save(
            call.toEntity(
                user = existingCall.creator,
                retrieveSpecificObjective = { programmeSpecificObjectiveRepo.getOne(it) },
                retrieveStrategies = { programmeStrategyRepo.getAllByStrategyInAndActiveTrue(it).toSet() },
                retrieveFunds = { programmeFundRepo.getTop20ByIdInAndSelectedTrue(it).toSet() },
                existingEntity = existingCall,
            )
        ).toDetailModel()
    }

    @Transactional
    override fun updateProjectCallFlatRate(callId: Long, flatRates: Set<ProjectCallFlatRate>): CallDetail {
        val call = callRepo.findById(callId).orElseThrow { CallNotFound() }
        call.updateFlatRateSetup(flatRates.toEntity(call))
        return call.toDetailModel()
    }

    @Transactional(readOnly = true)
    override fun existsAllProgrammeLumpSumsByIds(ids: Set<Long>): Boolean =
        programmeLumpSumRepo.findAllById(ids).size == ids.size

    @Transactional
    override fun updateProjectCallLumpSum(callId: Long, lumpSumIds: Set<Long>): CallDetail {
        val call = callRepo.findById(callId).orElseThrow { CallNotFound() }
        call.lumpSums.clear()
        call.lumpSums.addAll(programmeLumpSumRepo.findAllById(lumpSumIds))
        return call.toDetailModel()
    }

    @Transactional(readOnly = true)
    override fun existsAllProgrammeUnitCostsByIds(ids: Set<Long>): Boolean =
        programmeUnitCostRepo.findAllById(ids).size == ids.size

    @Transactional
    override fun updateProjectCallUnitCost(callId: Long, unitCostIds: Set<Long>): CallDetail {
        val call = callRepo.findById(callId).orElseThrow { CallNotFound() }
        call.unitCosts.clear()
        call.unitCosts.addAll(programmeUnitCostRepo.findAllById(unitCostIds))
        return call.toDetailModel()
    }

    @Transactional
    override fun publishCall(callId: Long) =
        callRepo.findById(callId).orElseThrow { CallNotFound() }
            .apply { status = CallStatus.PUBLISHED }
            .toModel()

    @Transactional(readOnly = true)
    override fun hasAnyCallPublished() =
        callRepo.existsByStatus(CallStatus.PUBLISHED)

}
