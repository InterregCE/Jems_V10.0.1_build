package io.cloudflight.jems.server.security.model

import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import io.cloudflight.jems.server.security.ADMINISTRATOR
import io.cloudflight.jems.server.security.PROGRAMME_USER
import org.springframework.security.core.GrantedAuthority

interface CurrentUser {
    val user: OutputUserWithRole

    val isAdmin: Boolean
        get() = hasRole(ADMINISTRATOR)

    val isProgrammeUser: Boolean
        get() = hasRole(PROGRAMME_USER)

    fun getAuthorities(): Collection<GrantedAuthority>

    fun hasRole(role: String): Boolean {
        return getAuthorities().stream().anyMatch { r -> r.authority.equals("ROLE_$role", ignoreCase = true) }
    }
}
