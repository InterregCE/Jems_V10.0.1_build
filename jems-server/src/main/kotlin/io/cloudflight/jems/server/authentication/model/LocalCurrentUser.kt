package io.cloudflight.jems.server.authentication.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class LocalCurrentUser(
    override val user: io.cloudflight.jems.server.user.service.model.User,
    password: String,
    authorities: Collection<GrantedAuthority> = emptyList()
) : User(user.email, password, authorities), CurrentUser
