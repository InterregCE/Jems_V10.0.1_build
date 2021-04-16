package io.cloudflight.jems.server.user.service.userrole.update_user_role

import io.cloudflight.jems.server.user.service.model.UserRole

interface UpdateUserRoleInteractor {

    fun updateUserRole(userRole: UserRole): UserRole

}
