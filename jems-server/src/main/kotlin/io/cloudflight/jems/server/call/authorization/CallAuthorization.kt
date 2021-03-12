package io.cloudflight.jems.server.call.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.call.repository.CallNotFound
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@callAuthorization.canUpdateCalls()")
annotation class CanUpdateCalls

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@callAuthorization.canReadCall(#callId)")
annotation class CanReadCall

@Component
class CallAuthorization(
    override val securityService: SecurityService,
    val callPersistence: CallPersistence,
) : Authorization(securityService) {

    fun canUpdateCalls(): Boolean = isAdmin() || isProgrammeUser()

    fun canReadCall(callId: Long): Boolean {
        if (isApplicantUser()) {
            val call: CallDetail
            try {
                call = callPersistence.getCallById(callId)
            } catch (e: CallNotFound) {
                return false
            }
            return call.isPublished()
        }

        if (isAdmin() || isProgrammeUser())
            return true

        return false
    }

}
