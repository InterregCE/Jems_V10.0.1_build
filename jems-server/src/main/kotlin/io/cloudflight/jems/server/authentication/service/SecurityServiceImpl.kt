package io.cloudflight.jems.server.authentication.service

import io.cloudflight.jems.server.authentication.model.CurrentUser
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service


@Service
class SecurityServiceImpl : SecurityService {
    override val currentUser: CurrentUser?
        get() {
            if (SecurityContextHolder.getContext().authentication !is UsernamePasswordAuthenticationToken) {
                return null;
            }
            return SecurityContextHolder.getContext().authentication.principal as CurrentUser;
        }

    override fun getUserIdOrThrow(): Long =
        currentUser?.user?.id ?: throw CurrentUseIdIsNullException()

}
