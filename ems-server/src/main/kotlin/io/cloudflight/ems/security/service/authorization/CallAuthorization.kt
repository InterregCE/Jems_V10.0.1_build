package io.cloudflight.ems.security.service.authorization

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

}
