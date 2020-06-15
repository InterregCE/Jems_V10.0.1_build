package io.cloudflight.ems.security.model

import io.cloudflight.ems.api.dto.OutputUser
import org.springframework.security.core.GrantedAuthority

class LocalCurrentUser(
    override val user: OutputUser,
    password: String,
    authorities: Collection<GrantedAuthority> = emptyList()
) : org.springframework.security.core.userdetails.User(user.email, password, authorities), CurrentUser
