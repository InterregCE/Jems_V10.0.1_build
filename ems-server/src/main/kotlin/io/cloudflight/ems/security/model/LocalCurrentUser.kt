package io.cloudflight.ems.security.model

import io.cloudflight.ems.api.dto.user.OutputUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class LocalCurrentUser(
    override val user: OutputUser,
    password: String,
    authorities: Collection<GrantedAuthority> = emptyList()
) : User(user.email, password, authorities), CurrentUser
