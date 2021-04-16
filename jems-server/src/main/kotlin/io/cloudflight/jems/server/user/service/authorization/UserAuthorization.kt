package io.cloudflight.jems.server.user.service.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.authentication.authorization.Authorization
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
//@PreAuthorize("hasAuthority('UserRetrieve')")
@PreAuthorize("@userAuthorization.admin")
annotation class CanRetrieveUsers

@Retention(AnnotationRetention.RUNTIME)
//@PreAuthorize("hasAuthority('UserRetrieve') || @userAuthorization.isThisUser(#userId)")
@PreAuthorize("@userAuthorization.admin || @userAuthorization.isThisUser(#userId)")
annotation class CanRetrieveUser

@Retention(AnnotationRetention.RUNTIME)
//@PreAuthorize("hasAuthority('UserCreate')")
@PreAuthorize("@userAuthorization.admin")
annotation class CanCreateUser

@Retention(AnnotationRetention.RUNTIME)
//@PreAuthorize("hasAuthority('UserUpdate')")
@PreAuthorize("@userAuthorization.admin")
annotation class CanUpdateUser

@Retention(AnnotationRetention.RUNTIME)
//@PreAuthorize("hasAuthority('UserUpdatePassword')")
@PreAuthorize("@userAuthorization.admin")
annotation class CanUpdateUserPassword

@Component
class UserAuthorization(
    override val securityService: SecurityService,
) : Authorization(securityService) {

    fun isThisUser(userId: Long): Boolean =
        securityService.currentUser?.user?.id == userId

}
