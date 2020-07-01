package io.cloudflight.ems.security.model

import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.security.ADMINISTRATOR
import org.springframework.security.core.GrantedAuthority

interface CurrentUser {
    val user: OutputUser

    val isAdmin: Boolean
        get() = hasRole(ADMINISTRATOR)

    fun getAuthorities(): Collection<GrantedAuthority>

    fun hasRole(role: String): Boolean {
        return getAuthorities().stream().anyMatch { r -> r.authority.equals("ROLE_$role", ignoreCase = true) }
    }
}
