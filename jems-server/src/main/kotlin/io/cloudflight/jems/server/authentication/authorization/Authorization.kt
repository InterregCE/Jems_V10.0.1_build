package io.cloudflight.jems.server.authentication.authorization

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.stereotype.Component

@Component
class Authorization(
    open val securityService: SecurityService
) {

    fun hasPermission(permission: UserRolePermission): Boolean =
        securityService.currentUser?.hasPermission(permission)!!

    protected fun isActiveUserIdEqualTo(userId: Long): Boolean =
        userId == securityService.currentUser?.user?.id

}
