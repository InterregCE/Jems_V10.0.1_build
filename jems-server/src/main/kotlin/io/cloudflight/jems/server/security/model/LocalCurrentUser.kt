package io.cloudflight.jems.server.security.model

import io.cloudflight.jems.api.user.dto.OutputUserWithRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class LocalCurrentUser(
    override val user: OutputUserWithRole,
    password: String,
    authorities: Collection<GrantedAuthority> = emptyList()
) : User(user.email, password, authorities), CurrentUser
