package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.CallFundRateEntity
import io.cloudflight.jems.server.call.entity.FundSetupId
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.AllowedRealCosts
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.CallApplicationFormFieldsConfiguration
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.CallFundRate
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.programme.repository.StrategyRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeUnitCostRepository
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.priority.ProgrammeSpecificObjectiveRepository
import io.cloudflight.jems.server.programme.repository.stateaid.ProgrammeStateAidRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.user.repository.user.UserRepository
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
    private val projectCallStateAidRepo: ProjectCallStateAidRepository,
    private val programmeStateAidRepository: ProgrammeStateAidRepository,
    private val applicationFormFieldConfigurationRepository: ApplicationFormFieldConfigurationRepository,
    private val projectPersistence: ProjectPersistence,
) : CallPersistence {

    @Transactional(readOnly = true)
    override fun getCalls(pageable: Pageable): Page<CallSummary> =
        callRepo.findAll(pageable).toModel()

    @Transactional(readOnly = true)
    override fun getPublishedAndOpenCalls(pageable: Pageable): Page<CallSummary> =
        callRepo.findAllByStatusAndEndDateAfter(CallStatus.PUBLISHED, ZonedDateTime.now(), pageable).toModel()

    @Transactional(readOnly = true)
    override fun getCallById(callId: Long): CallDetail =
        callRepo.findById(callId).map {
            it.toDetailModel(
                applicationFormFieldConfigurationRepository.findAllByCallId(callId),
                projectCallStateAidRepo.findAllByIdCallId(callId)
            )
        }.orElseThrow { CallNotFound() }

    @Transactional(readOnly = true)
    override fun getCallByProjectId(projectId: Long): CallDetail =
        getCallById(projectPersistence.getCallIdOfProject(projectId))

    @Transactional(readOnly = true)
    override fun getCallIdForNameIfExists(name: String): Long? =
        callRepo.findFirstByName(name)?.id

    @Transactional
    override fun createCall(call: Call, userId: Long): CallDetail {

        adjustTimeToLastNanoSec(call)

        val created = callRepo.save(
            call.toEntity(
                user = userRepo.getById(userId),
                retrieveSpecificObjective = { programmeSpecificObjectiveRepo.getById(it) },
                retrieveStrategies = { programmeStrategyRepo.getAllByStrategyInAndActiveTrue(it).toSet() }
            )
        )

        updateFunds(created, call.funds)

        return created.toDetailModel(
            applicationFormFieldConfigurationRepository.findAllByCallId(call.id),
            projectCallStateAidRepo.findAllByIdCallId(call.id)
        )
    }

    @Transactional
    override fun updateCall(call: Call): CallDetail {
        val existingCall = callRepo.findById(call.id).orElseThrow { CallNotFound() }

        adjustTimeToLastNanoSec(call)

        // check if the stateAids need to be update when call is updated and do so
        val existingStateAidsForCall = projectCallStateAidRepo.findAllByIdCallId(call.id).map { it.setupId.stateAid.id }

        if (call.stateAidIds != existingStateAidsForCall) {
            val stateAidsToDelete = existingStateAidsForCall.filter { !call.stateAidIds.contains(it) }
            stateAidsToDelete.forEach { projectCallStateAidRepo.deleteAllBySetupIdStateAidId(it) }
            projectCallStateAidRepo.saveAll(
                programmeStateAidRepository.findAllById(call.stateAidIds).toMutableSet().toEntities(existingCall)
            )
        }

        updateFunds(existingCall, call.funds)

        return callRepo.save(
            call.toEntity(
                user = existingCall.creator,
                retrieveSpecificObjective = { programmeSpecificObjectiveRepo.getById(it) },
                retrieveStrategies = { programmeStrategyRepo.getAllByStrategyInAndActiveTrue(it).toSet() },
                existingEntity = existingCall,
            )
        ).toDetailModel(
            applicationFormFieldConfigurationRepository.findAllByCallId(call.id),
            projectCallStateAidRepo.findAllByIdCallId(call.id)
        )
    }

    private fun updateFunds(existingCall: CallEntity, newFunds: Set<CallFundRate>) {
        // remove deselected funds
        val newFundIds = newFunds.map { it.programmeFund.id }.toSet()
        existingCall.funds.removeIf { !newFundIds.contains(it.setupId.programmeFund.id) }

        // add or update funds
        val newFundEntities = programmeFundRepo.getTop20ByIdInAndSelectedTrue(newFundIds)
            .associateBy { it.id }.toMutableMap()

        newFunds.forEach {
            val fund =
                existingCall.funds.find { fundEntity -> fundEntity.setupId.programmeFund.id == it.programmeFund.id }
            if (fund != null) {
                fund.rate = it.rate
                fund.isAdjustable = it.adjustable
            } else {
                existingCall.funds.add(
                    CallFundRateEntity(
                        setupId = FundSetupId(existingCall, newFundEntities[(it.programmeFund.id)]!!),
                        rate = it.rate,
                        isAdjustable = it.adjustable
                    )
                )
            }
        }
    }

    @Transactional
    override fun updateProjectCallFlatRate(callId: Long, flatRatesRequest: Set<ProjectCallFlatRate>): CallDetail {
        val call = callRepo.findById(callId).orElseThrow { CallNotFound() }
            .apply {
                val groupedByType = flatRatesRequest.toEntity(this).associateBy { it.setupId.type }.toMutableMap()
                flatRates.forEach {
                    if (groupedByType.keys.contains(it.setupId.type)) {
                        val newValue = groupedByType.getValue(it.setupId.type)
                        it.rate = newValue.rate
                        it.isAdjustable = newValue.isAdjustable
                    }
                }
                flatRates.removeIf { !groupedByType.keys.contains(it.setupId.type) }
                val existingTypes = flatRates.associateBy { it.setupId.type }.keys
                groupedByType.filterKeys { !existingTypes.contains(it) }
                    .forEach {
                        flatRates.add(it.value)
                    }
            }
        return call.toDetailModel(
            applicationFormFieldConfigurationRepository.findAllByCallId(callId),
            projectCallStateAidRepo.findAllByIdCallId(callId)
        )
    }

    @Transactional(readOnly = true)
    override fun existsAllProgrammeLumpSumsByIds(ids: Set<Long>): Boolean =
        programmeLumpSumRepo.findAllById(ids).size == ids.size

    @Transactional
    override fun updateProjectCallLumpSum(callId: Long, lumpSumIds: Set<Long>): CallDetail {
        val call = callRepo.findById(callId).orElseThrow { CallNotFound() }
        call.lumpSums.clear()
        call.lumpSums.addAll(programmeLumpSumRepo.findAllById(lumpSumIds))
        return call.toDetailModel(
            applicationFormFieldConfigurationRepository.findAllByCallId(callId),
            projectCallStateAidRepo.findAllByIdCallId(callId)
        )
    }

    @Transactional(readOnly = true)
    override fun existsAllProgrammeUnitCostsByIds(ids: Set<Long>): Boolean =
        programmeUnitCostRepo.findAllById(ids).size == ids.size

    @Transactional
    override fun updateProjectCallUnitCost(callId: Long, unitCostIds: Set<Long>): CallDetail {
        val call = callRepo.findById(callId).orElseThrow { CallNotFound() }
        call.unitCosts.clear()
        call.unitCosts.addAll(programmeUnitCostRepo.findAllById(unitCostIds))
        return call.toDetailModel(
            applicationFormFieldConfigurationRepository.findAllByCallId(callId),
            projectCallStateAidRepo.findAllByIdCallId(callId)
        )
    }


    @Transactional
    override fun updateAllowedRealCosts(callId: Long, allowedRealCosts: AllowedRealCosts): AllowedRealCosts {
        val call = callRepo.findById(callId).orElseThrow { CallNotFound() }
        call.allowedRealCosts = allowedRealCosts.toEntity()
        return allowedRealCosts
    }

    @Transactional(readOnly = true)
    override fun getAllowedRealCosts(callId: Long): AllowedRealCosts =
        callRepo.findById(callId).orElseThrow { CallNotFound() }.allowedRealCosts.toModel()

    @Transactional
    override fun publishCall(callId: Long) =
        callRepo.findById(callId).orElseThrow { CallNotFound() }
            .apply { status = CallStatus.PUBLISHED }
            .toModel()

    @Transactional(readOnly = true)
    override fun hasAnyCallPublished() =
        callRepo.existsByStatus(CallStatus.PUBLISHED)

    @Transactional(readOnly = true)
    override fun isCallPublished(callId: Long) =
        callRepo.existsByidAndStatus(callId, CallStatus.PUBLISHED)

    @Transactional(readOnly = true)
    override fun listCalls(status: CallStatus?): List<IdNamePair> =
        if (status != null)
            callRepo.findAllByStatus(status).toIdNamePair()
        else
            callRepo.findAll().toIdNamePair()

    @Transactional(readOnly = true)
    override fun getApplicationFormFieldConfigurations(callId: Long): CallApplicationFormFieldsConfiguration {
        val callType = callRepo.findById(callId).orElseThrow { CallNotFound() }.type
        return CallApplicationFormFieldsConfiguration(
            callType = callType,
            applicationFormFieldConfigurations = applicationFormFieldConfigurationRepository.findAllByCallId(callId).toModel()
        )
    }

    @Transactional
    override fun saveApplicationFormFieldConfigurations(
        callId: Long, applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfiguration>
    ): CallDetail {
        val callEntity = callRepo.findById(callId).orElseThrow { CallNotFound() }

        val configurations =
            applicationFormFieldConfigurationRepository.saveAll(applicationFormFieldConfigurations.toEntities(callEntity))
                .toMutableSet()
        return callEntity.toDetailModel(
            configurations,
            projectCallStateAidRepo.findAllByIdCallId(callId)
        )
    }

    @Transactional
    override fun updateProjectCallStateAids(callId: Long, stateAids: Set<Long>): CallDetail {
        val callEntity = callRepo.findById(callId).orElseThrow { CallNotFound() }

        val savedStateAids = projectCallStateAidRepo.saveAll(
            programmeStateAidRepository.findAllById(stateAids).toMutableSet().toEntities(callEntity)
        ).toMutableSet()

        return callEntity.toDetailModel(
            applicationFormFieldConfigurationRepository.findAllByCallId(callId),
            savedStateAids
        )
    }

    @Transactional
    override fun updateProjectCallPreSubmissionCheckPlugin(callId: Long, pluginKey: String?) =
        callRepo.findById(callId).orElseThrow { CallNotFound() }
        .apply { preSubmissionCheckPluginKey = pluginKey }.toDetailModel(
                applicationFormFieldConfigurationRepository.findAllByCallId(callId),
                projectCallStateAidRepo.findAllByIdCallId(callId)
            )

    private fun adjustTimeToLastNanoSec(call: Call) {
        call.startDate = call.startDate.withSecond(0).withNano(0)
        call.endDateStep1 = call.endDateStep1?.withSecond(0)?.withNano(0)?.plusMinutes(1)?.minusNanos(1)
        call.endDate = call.endDate.withSecond(0).withNano(0).plusMinutes(1).minusNanos(1)
    }
}
