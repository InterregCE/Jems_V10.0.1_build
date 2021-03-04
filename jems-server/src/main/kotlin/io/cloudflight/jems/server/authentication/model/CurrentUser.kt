package io.cloudflight.jems.server.authentication.model

import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import org.springframework.security.core.GrantedAuthority

interface CurrentUser {
    val user: OutputUserWithRole

    val isAdmin: Boolean
        get() = hasRole(ADMINISTRATOR)

    val isProgrammeUser: Boolean
        get() = hasRole(PROGRAMME_USER)

    val isApplicant: Boolean
        get() = hasRole(APPLICANT_USER)

    fun getAuthorities(): Collection<GrantedAuthority>

    fun hasRole(role: String): Boolean {
        return getAuthorities().stream().anyMatch { r -> r.authority.equals("ROLE_$role", ignoreCase = true) }
    }
}
