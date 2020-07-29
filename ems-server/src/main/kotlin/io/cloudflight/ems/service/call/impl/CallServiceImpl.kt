package io.cloudflight.ems.service.call.impl

import io.cloudflight.ems.api.dto.call.InputCallCreate
import io.cloudflight.ems.api.dto.call.InputCallUpdate
import io.cloudflight.ems.api.dto.call.OutputCall
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.CallRepository
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.AuditService
import io.cloudflight.ems.service.call.CallService
import io.cloudflight.ems.service.call.mapper.toEntity
import io.cloudflight.ems.service.call.mapper.toOutputCall
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CallServiceImpl(
    private val callRepository: CallRepository,
    private val userRepository: UserRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
) : CallService {

    @Transactional
    override fun createCall(inputCall: InputCallCreate): OutputCall {
        val creator = userRepository.findByIdOrNull(securityService.currentUser?.user?.id!!)
            ?: throw ResourceNotFoundException()

        val savedCall = callRepository.save(inputCall.toEntity(creator)).toOutputCall()

        auditService.logEvent(Audit.callCreated(
            currentUser = securityService.currentUser,
            callId = savedCall.id.toString(),
            call = savedCall
        ))
        return savedCall;
    }

    @Transactional
    override fun updateCall(inputCall: InputCallUpdate): OutputCall {
        val call = callRepository.findById(inputCall.id).orElseThrow { ResourceNotFoundException() }

        val toUpdate = call.copy(
            name = inputCall.name,
            startDate = inputCall.startDate!!,
            endDate = inputCall.endDate!!,
            description = inputCall.description!!
        )

        val updatedCall = callRepository.save(toUpdate).toOutputCall()

        auditService.logEvent(
            Audit.callUpdated(
                currentUser = securityService.currentUser,
                callId = updatedCall.id.toString(),
                call = updatedCall
            )
        )

        return updatedCall

    }

    @Transactional(readOnly = true)
    override fun getCallById(id: Long): OutputCall {
        return callRepository.findById(id).map { it.toOutputCall() }
            .orElseThrow { ResourceNotFoundException() }
    }

    @Transactional(readOnly = true)
    override fun getCalls(pageable: Pageable): Page<OutputCall> {
        return callRepository.findAll(pageable).map { it.toOutputCall() }
    }

}
