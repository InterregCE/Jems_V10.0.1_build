package io.cloudflight.ems.call.controller

import io.cloudflight.ems.api.call.CallApi
import io.cloudflight.ems.api.call.dto.InputCallCreate
import io.cloudflight.ems.api.call.dto.InputCallUpdate
import io.cloudflight.ems.api.call.dto.OutputCall
import io.cloudflight.ems.api.call.dto.OutputCallProgrammePriority
import io.cloudflight.ems.api.programme.dto.OutputProgrammePriority
import io.cloudflight.ems.call.service.CallService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class CallController(
    private val callService: CallService
) : CallApi {

    /**
     * Here the @PreAuthorize annotation is missing because list is filtered based on restrictions inside the service
     */
    override fun getCalls(pageable: Pageable): Page<OutputCall> {
        return callService.getCalls(pageable)
    }

    @PreAuthorize("@callAuthorization.canReadCallDetail(#id)")
    override fun getCallById(id: Long): OutputCall {
        return callService.getCallById(id)
    }

    @PreAuthorize("@callAuthorization.canCreateCall()")
    override fun createCall(call: InputCallCreate): OutputCall {
        return callService.createCall(call);
    }

    @PreAuthorize("@callAuthorization.canUpdateCall(#call.id)")
    override fun updateCall(call: InputCallUpdate): OutputCall {
        return callService.updateCall(call)
    }

    @PreAuthorize("@callAuthorization.canUpdateCall(#id)")
    override fun publishCall(id: Long): OutputCall {
        return callService.publishCall(id)
    }

    override fun getCallObjectives(callId: Long): List<OutputCallProgrammePriority> {
        return callService.getPriorityAndPoliciesForCall(callId)
    }

}
