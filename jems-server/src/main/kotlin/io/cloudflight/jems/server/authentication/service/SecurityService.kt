package io.cloudflight.jems.server.authentication.service

import io.cloudflight.jems.server.authentication.model.CurrentUser

interface SecurityService {
    val currentUser: CurrentUser?

    fun getUserIdOrThrow(): Long
    fun assertAdminAccess()
}
