package io.cloudflight.skeleton.security.model

import io.cloudflight.skeleton.dto.User
import org.springframework.security.core.GrantedAuthority

class LocalCurrentUser(
    override val user: User,
    authorities: Collection<GrantedAuthority> = emptyList()
) : org.springframework.security.core.userdetails.User(user.username, "", authorities), CurrentUser
