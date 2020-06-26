package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.security.service.SecurityService
import org.springframework.stereotype.Component

@Component
class UserAuthorization(val securityService: SecurityService) {

    fun canUpdateUser(email: String): Boolean =
        securityService.currentUser?.isAdmin!! ||
                securityService.currentUser?.user?.email == email
}
