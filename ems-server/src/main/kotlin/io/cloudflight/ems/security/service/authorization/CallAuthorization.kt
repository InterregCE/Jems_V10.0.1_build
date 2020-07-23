package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.call.CallService
import org.springframework.stereotype.Component

@Component
class CallAuthorization(
    val securityService: SecurityService,
    val callService: CallService
) {

    fun canCreateCall(): Boolean {
        return isAdmin() || isProgrammeUser()
    }

    fun isAdmin(): Boolean {
        return securityService.currentUser?.isAdmin!!
    }

    fun isProgrammeUser(): Boolean {
        return securityService.currentUser?.isProgrammeUser!!
    }

}
