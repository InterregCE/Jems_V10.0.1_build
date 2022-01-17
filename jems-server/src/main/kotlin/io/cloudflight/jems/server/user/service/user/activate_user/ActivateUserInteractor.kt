package io.cloudflight.jems.server.user.service.user.activate_user

import java.util.UUID

interface ActivateUserInteractor {
    fun activateUser(token: UUID): Boolean
}
