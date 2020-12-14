package io.cloudflight.jems.server.call.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.InputCallCreate
import io.cloudflight.jems.api.call.dto.InputCallUpdate
import io.cloudflight.jems.api.call.dto.OutputCall
import io.cloudflight.jems.api.call.dto.OutputCallList
import io.cloudflight.jems.api.call.dto.OutputCallProgrammePriority
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.call.repository.flatrate.CallRepository
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.repository.ProgrammePriorityPolicyRepository
import io.cloudflight.jems.server.programme.service.toOutputProgrammePriorityPolicy
import io.cloudflight.jems.server.programme.service.toOutputProgrammePrioritySimple
import io.cloudflight.jems.server.user.repository.UserRepository
import io.cloudflight.jems.server.authentication.model.APPLICANT_USER
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.entity.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.repository.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.entity.Strategy
import io.cloudflight.jems.server.programme.repository.StrategyRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CallServiceImpl(
    private val callRepository: CallRepository,
    private val userRepository: UserRepository,
    private val programmePriorityPolicyRepository: ProgrammePriorityPolicyRepository,
    private val strategyRepository: StrategyRepository,
    private val fundRepository: ProgrammeFundRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
) : CallService {

    @Transactional(readOnly = true)
    override fun getCallById(id: Long): OutputCall {
        return callRepository.findById(id).map { it.toOutputCall() }
            .orElseThrow { ResourceNotFoundException("call") }
    }

    @Transactional(readOnly = true)
    override fun getCalls(pageable: Pageable): Page<OutputCallList> {
        val currentUser = securityService.currentUser!!
        if (currentUser.isAdmin || currentUser.isProgrammeUser)
            return callRepository.findAll(pageable).map { it.toOutputCallList() }
        if (currentUser.hasRole(APPLICANT_USER))
            return callRepository.findAllByStatus(CallStatus.PUBLISHED, pageable).map { it.toOutputCallList() }
        return Page.empty()
    }

    @Transactional
    override fun createCall(inputCall: InputCallCreate): OutputCall {
        val creator = userRepository.findById(securityService.currentUser?.user?.id!!)
            .orElseThrow { ResourceNotFoundException() }

        val savedCall = callRepository.save(inputCall
            .toEntity(
                creator = creator,
                priorityPolicies = inputCall.priorityPolicies?.let { getPoliciesAsEntities(it) } ?: emptySet(),
                strategies = getStrategiesAsEntities(inputCall.strategies),
                funds = getFundsAsEntities(inputCall.funds)
            )
        ).toOutputCall()

        AuditBuilder(AuditAction.CALL_CREATED)
            .description("A new call id=${savedCall.id} '${savedCall.name}' was created")
            .logWithService(auditService)

        return savedCall
    }

    @Transactional
    override fun updateCall(inputCall: InputCallUpdate): OutputCall {
        val oldCall = callRepository.findById(inputCall.id)
            .orElseThrow { ResourceNotFoundException("call") }

        val toUpdate = oldCall.copy(
            name = getCallNameIfUnique(oldCall, inputCall.name!!),
            priorityPolicies = getPoliciesAsEntities(inputCall.priorityPolicies!!),
            strategies = getStrategiesAsEntities(inputCall.strategies),
            funds = getFundsAsEntities(inputCall.funds),
            startDate = inputCall.startDate!!,
            endDate = inputCall.endDate!!,
            description = inputCall.description,
            lengthOfPeriod = inputCall.lengthOfPeriod
        )

        return callRepository.save(toUpdate).toOutputCall()
    }

    private fun getCallNameIfUnique(oldCall: CallEntity, newName: String): String {
        if (oldCall.name == newName)
            return oldCall.name

        val existing = callRepository.findOneByName(newName)
        if (existing == null || existing.id == oldCall.id)
            return newName

        throw I18nValidationException(
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
            i18nFieldErrors = mapOf("name" to I18nFieldError("call.name.already.in.use"))
        )
    }

    private fun getPoliciesAsEntities(policies: Collection<ProgrammeObjectivePolicy>): Set<ProgrammePriorityPolicy> {
        val result = programmePriorityPolicyRepository.findAllById(policies).toSet()

        if (policies.size != result.size)
            throw ResourceNotFoundException("programme_priority_policy")
        else
            return result
    }

    private fun getStrategiesAsEntities(strategies: Collection<ProgrammeStrategy>?): Set<Strategy> {
        if (strategies == null) return emptySet()
        val result = strategyRepository.findAllById(strategies)
            .filter { it.active }
            .toSet()

        if (strategies.size != result.size)
            throw ResourceNotFoundException("programme_strategy")
        else
            return result
    }

    private fun getFundsAsEntities(funds: Set<Long>?): Set<ProgrammeFundEntity> {
        if (funds == null) return emptySet()
        val result = fundRepository.findAllById(funds)
            .filter { it.selected }
            .toSet()

        if (funds.size != result.size)
            throw ResourceNotFoundException("programme_fund")
        else
            return result
    }

    @Transactional
    override fun publishCall(callId: Long): OutputCall {
        val call = callRepository.findById(callId)
            .orElseThrow { ResourceNotFoundException("call") }

        validateIsDraft(call)
        validatePublishingRequirementsAchieved(call)

        val updatedCall = callRepository.save(call.copy(status = CallStatus.PUBLISHED)).toOutputCall()

        AuditBuilder(AuditAction.CALL_PUBLISHED)
            .description("Call id=${call.id} '${call.name}' published")
            .logWithService(auditService)
        return updatedCall
    }

    private fun validateIsDraft(call: CallEntity) {
        if (call.status != CallStatus.DRAFT)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "call.state.cannot.publish"
            )
    }

    private fun validatePublishingRequirementsAchieved(call: CallEntity) {
        if (call.priorityPolicies.isEmpty())
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "call.priorityPolicies.is.empty"
            )
        if (call.funds.isEmpty())
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "call.funds.is.empty"
            )
    }

    @Transactional(readOnly = true)
    override fun findOneByName(name: String): OutputCall? {
        return callRepository.findOneByName(name)?.toOutputCall()
    }

    @Transactional(readOnly = true)
    override fun getPriorityAndPoliciesForCall(callId: Long): List<OutputCallProgrammePriority> {
        val call = callRepository.findById(callId)
            .orElseThrow { ResourceNotFoundException("call") }

        return call.priorityPolicies
            .groupBy { it.programmePriority!!.toOutputProgrammePrioritySimple() }
            .map { (programmePriority, policies) ->
                OutputCallProgrammePriority(
                    code = programmePriority.code,
                    title = programmePriority.title,
                    programmePriorityPolicies = policies.map { it.toOutputProgrammePriorityPolicy() }
                )
            }
    }

}
