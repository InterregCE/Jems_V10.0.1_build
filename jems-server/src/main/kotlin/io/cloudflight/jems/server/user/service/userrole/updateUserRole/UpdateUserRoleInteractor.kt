package io.cloudflight.jems.server.user.service.userrole.updateUserRole

import io.cloudflight.jems.server.user.service.model.UserRole

interface UpdateUserRoleInteractor {

    fun updateUserRole(userRole: UserRole): UserRole

}
