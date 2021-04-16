package io.cloudflight.jems.server.user.service.user.update_user

import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange

interface UpdateUserInteractor {

    fun updateUser(user: UserChange): User

}
