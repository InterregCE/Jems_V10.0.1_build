package io.cloudflight.ems.controller

import io.cloudflight.ems.api.CallApi
import io.cloudflight.ems.api.dto.call.InputCallCreate
import io.cloudflight.ems.api.dto.call.InputCallUpdate
import io.cloudflight.ems.api.dto.call.OutputCall
import io.cloudflight.ems.service.call.CallService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class CallController(
    private val callService: CallService
) : CallApi {

    @PreAuthorize("@callAuthorization.canCreateCall()")
    override fun createCall(call: InputCallCreate): OutputCall {
        return callService.createCall(call);
    }

    override fun updateCall(call: InputCallUpdate): OutputCall {
        return callService.updateCall(call)
    }

    override fun getCalls(pageable: Pageable): Page<OutputCall> {
        return callService.getCalls(pageable)
    }

    override fun getCallById(id: Long): OutputCall {
        return callService.getCallById(id)
    }
}
