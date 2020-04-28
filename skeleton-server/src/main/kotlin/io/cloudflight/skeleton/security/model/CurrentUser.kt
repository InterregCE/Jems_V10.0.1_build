package io.cloudflight.skeleton.security.model

import io.cloudflight.skeleton.security.ROLE_ADMIN
import io.cloudflight.skeleton.dto.User
import org.springframework.security.core.GrantedAuthority

interface CurrentUser {
    val user: User

    val isAdmin: Boolean
        get() = hasRole(ROLE_ADMIN)

    fun getAuthorities(): Collection<GrantedAuthority>

    fun hasRole(role: String): Boolean {
        return getAuthorities().stream().anyMatch { r -> r.authority.equals(role, ignoreCase = true) }
    }
}
