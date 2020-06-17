package io.cloudflight.ems.security.service

import io.cloudflight.ems.security.model.CurrentUser

interface SecurityService {
    val currentUser: CurrentUser?

    fun assertAdminAccess()
}
