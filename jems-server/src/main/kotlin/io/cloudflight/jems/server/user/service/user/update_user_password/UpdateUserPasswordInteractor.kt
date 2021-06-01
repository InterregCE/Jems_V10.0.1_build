package io.cloudflight.jems.server.user.service.user.update_user_password

import io.cloudflight.jems.server.user.service.model.Password

interface UpdateUserPasswordInteractor {

    fun resetUserPassword(userId: Long, newPassword: String)

    fun updateMyPassword(passwordData: Password)

}
