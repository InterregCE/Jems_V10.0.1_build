package io.cloudflight.ems.user.authorization

import io.cloudflight.ems.api.user.dto.InputUserUpdate
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.user.service.UserService
import org.springframework.stereotype.Component

@Component
class UserAuthorization(
    val securityService: SecurityService,
    val userService: UserService
) {

    fun canUpdateUser(userId: Long): Boolean =
        securityService.currentUser?.isAdmin!! ||
                securityService.currentUser?.user?.id == userId

    fun canUpdateUser(userUpdate: InputUserUpdate): Boolean {
        if (securityService.currentUser?.isAdmin!!) {
            return true;
        }
        if (securityService.currentUser?.user?.id != userUpdate.id) {
            // limited user can only update its own user
            return false;
        }
        // limited user cannot change its own role
        return userService.getById(userUpdate.id).userRole.id == userUpdate.userRoleId;
    }
}
