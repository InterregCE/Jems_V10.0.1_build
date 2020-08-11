package io.cloudflight.ems.call.service

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.api.call.dto.InputCallCreate
import io.cloudflight.ems.api.call.dto.InputCallUpdate
import io.cloudflight.ems.api.call.dto.OutputCall
import io.cloudflight.ems.api.programme.dto.ProgrammeObjectivePolicy
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.call.entity.Call
import io.cloudflight.ems.exception.I18nFieldError
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.call.repository.CallRepository
import io.cloudflight.ems.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.ems.programme.repository.ProgrammePriorityPolicyRepository
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.security.APPLICANT_USER
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.AuditService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class CallServiceImpl(
    private val callRepository: CallRepository,
    private val userRepository: UserRepository,
    private val programmePriorityPolicyRepository: ProgrammePriorityPolicyRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
) : CallService {

    @Transactional(readOnly = true)
    override fun getCallById(id: Long): OutputCall {
        return callRepository.findById(id).map { it.toOutputCall() }
            .orElseThrow { ResourceNotFoundException("call") }
    }

    @Transactional(readOnly = true)
    override fun getCalls(pageable: Pageable): Page<OutputCall> {
        val currentUser = securityService.currentUser!!
        if (currentUser.isAdmin || currentUser.isProgrammeUser)
            return callRepository.findAll(pageable).map { it.toOutputCall() }
        if (currentUser.hasRole(APPLICANT_USER))
            return callRepository.findAllByStatusAndEndDateAfter(
                status = CallStatus.PUBLISHED,
                endDateAfter = ZonedDateTime.now(),
                pageable = pageable
            ).map { it.toOutputCall() }
        return Page.empty()
    }

    @Transactional
    override fun createCall(inputCall: InputCallCreate): OutputCall {
        val creator = userRepository.findById(securityService.currentUser?.user?.id!!)
            .orElseThrow { ResourceNotFoundException() }
        return callRepository.save(inputCall
            .toEntity(
                creator = creator,
                priorityPolicies = inputCall.priorityPolicies?.let { getPoliciesAsEntities(it) } ?: emptySet()
            )
        ).toOutputCall()
    }

    @Transactional
    override fun updateCall(inputCall: InputCallUpdate): OutputCall {
        val oldCall = callRepository.findById(inputCall.id)
            .orElseThrow { ResourceNotFoundException("call") }

        val toUpdate = oldCall.copy(
            name = getCallNameIfUnique(oldCall, inputCall.name!!),
            priorityPolicies = getPoliciesAsEntities(inputCall.priorityPolicies!!),
            startDate = inputCall.startDate!!,
            endDate = inputCall.endDate!!,
            description = inputCall.description
        )

        return callRepository.save(toUpdate).toOutputCall()
    }

    private fun getCallNameIfUnique(oldCall: Call, newName: String): String {
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

    private fun getPoliciesAsEntities(policies: Iterable<ProgrammeObjectivePolicy>): Set<ProgrammePriorityPolicy> {
        return policies.map {
            programmePriorityPolicyRepository.findById(it)
                .orElseThrow { ResourceNotFoundException("programme_priority_policy") }
        }.toSet()
    }

    @Transactional
    override fun publishCall(callId: Long): OutputCall {
        val call = callRepository.findById(callId)
            .orElseThrow { ResourceNotFoundException("call") }

        validateIsDraft(call)
        validatePoliciesNotEmpty(call)

        val updatedCall = callRepository.save(call.copy(status = CallStatus.PUBLISHED)).toOutputCall()

        auditService.logEvent(
            Audit.callPublished(
                currentUser = securityService.currentUser,
                call = updatedCall
            )
        )
        return updatedCall
    }

    private fun validateIsDraft(call: Call) {
        if (call.status != CallStatus.DRAFT)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "call.state.cannot.publish"
            )
    }

    private fun validatePoliciesNotEmpty(call: Call) {
        if (call.priorityPolicies.isEmpty())
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "call.priorityPolicies.is.empty"
            )
    }

    @Transactional(readOnly = true)
    override fun findOneByName(name: String): OutputCall? {
        return callRepository.findOneByName(name)?.toOutputCall()
    }

}
