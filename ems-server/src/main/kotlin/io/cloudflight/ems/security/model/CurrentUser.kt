package io.cloudflight.ems.security.model

import io.cloudflight.ems.api.user.dto.OutputUserWithRole
import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.security.PROGRAMME_USER
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
