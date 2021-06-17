package io.cloudflight.jems.server.call.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import io.cloudflight.jems.server.call.repository.CallNotFound
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("@callAuthorization.canRetrieveCall(#callId)")
annotation class CanRetrieveCall

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('CallRetrieve')")
annotation class CanRetrieveCalls

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('CallUpdate')")
annotation class CanUpdateCall

@Component
class CallAuthorization(
    override val securityService: SecurityService,
    val callPersistence: CallPersistence,
) : Authorization(securityService) {

    fun canRetrieveCall(callId: Long): Boolean =
        hasPermission(UserRolePermission.CallRetrieve) || callPersistence.getCallById(callId).isPublished()
}
