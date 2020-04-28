package io.cloudflight.skeleton.security.service.impl

import io.cloudflight.skeleton.security.model.CurrentUser
import io.cloudflight.skeleton.security.service.SecurityService
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service


@Service
class SecurityServiceImpl : SecurityService {
    override val currentUser: CurrentUser
        get() = (SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken).principal as CurrentUser

    override fun assertAdminAccess() {
        if (!currentUser.isAdmin) throw AccessDeniedException("User does not have admin access")
    }
}
