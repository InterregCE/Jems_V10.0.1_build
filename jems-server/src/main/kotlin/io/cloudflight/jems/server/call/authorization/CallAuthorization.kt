package io.cloudflight.jems.server.call.authorization

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.OutputCall
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.call.service.CallService
import org.springframework.stereotype.Component

@Component
class CallAuthorization(
    override val securityService: SecurityService,
    val callService: CallService
) : Authorization(securityService) {

    fun canCreateCall(): Boolean {
        return isAdmin() || isProgrammeUser()
    }

    fun canUpdateCall(callId: Long): Boolean {
        val callStatus = callService.getCallById(callId).status
        if (isAdmin() || isProgrammeUser())
            return callStatus == CallStatus.DRAFT

        if (!isAdmin() && !isProgrammeUser())
            throw ResourceNotFoundException("call")

        return false
    }

    fun canReadCallDetail(callId: Long): Boolean {
        if (isApplicantUser())
            if (isCallPublished(callService.getCallById(callId)))
                return true
            else throw ResourceNotFoundException("call")

        if (isAdmin() || isProgrammeUser())
            return true

        return false
    }

    private fun isCallPublished(call: OutputCall): Boolean {
        return call.status == CallStatus.PUBLISHED
    }

}
