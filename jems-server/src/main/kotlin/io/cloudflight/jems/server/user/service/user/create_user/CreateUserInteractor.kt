package io.cloudflight.jems.server.user.service.user.create_user

import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange

interface CreateUserInteractor {

    fun createUser(user: UserChange): User
}
