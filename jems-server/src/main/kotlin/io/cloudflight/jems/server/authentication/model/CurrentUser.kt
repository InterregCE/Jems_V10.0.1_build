package io.cloudflight.jems.server.authentication.model

import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.security.core.GrantedAuthority

interface CurrentUser {
    val user: User

    fun getAuthorities(): Collection<GrantedAuthority>

    fun hasPermission(permission: UserRolePermission): Boolean {
        return getAuthorities().stream().anyMatch { r -> r.authority.equals(permission.name, ignoreCase = true) }
    }
}
