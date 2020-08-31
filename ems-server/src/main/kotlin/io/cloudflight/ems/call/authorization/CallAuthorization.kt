package io.cloudflight.ems.call.authorization

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.api.call.dto.OutputCall
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.security.service.authorization.Authorization
import io.cloudflight.ems.call.service.CallService
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
