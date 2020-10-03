package io.cloudflight.jems.server.security.service

import io.cloudflight.jems.server.security.model.CurrentUser

interface SecurityService {
    val currentUser: CurrentUser?

    fun assertAdminAccess()
}
