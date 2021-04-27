package io.cloudflight.jems.server.user.service.userrole.create_user_role

import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleCreate

interface CreateUserRoleInteractor {

    fun createUserRole(userRole: UserRoleCreate): UserRole

}
