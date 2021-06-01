package io.cloudflight.jems.server.authentication.model

import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.core.GrantedAuthority

interface CurrentUser {
    val user: User

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

    fun hasPermission(permission: UserRolePermission): Boolean {
        return getAuthorities().stream().anyMatch { r -> r.authority.equals(permission.name, ignoreCase = true) }
    }
}
