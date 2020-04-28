package io.cloudflight.skeleton.security.service

import io.cloudflight.skeleton.security.model.CurrentUser

interface SecurityService {
    val currentUser: CurrentUser

    fun assertAdminAccess()
}
