package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.api.call.dto.OutputCall
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.call.CallService
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
        if (callStatus == CallStatus.DRAFT)
            return isAdmin() || isProgrammeUser()
        return false
    }

    fun canReadCallDetail(callId: Long): Boolean {
        if (isApplicantUser())
            return isCallPublished(callService.getCallById(callId))
        if (isAdmin() || isProgrammeUser())
            return true
        return false
    }

    private fun isCallPublished(call: OutputCall): Boolean {
        return call.status == CallStatus.PUBLISHED
    }

}
