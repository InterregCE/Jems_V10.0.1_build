package io.cloudflight.ems.security.model

import io.cloudflight.ems.security.ROLE_ADMIN
import io.cloudflight.ems.dto.User
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
