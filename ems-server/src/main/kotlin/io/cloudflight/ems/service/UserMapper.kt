package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.security.model.LocalCurrentUser
import org.springframework.security.core.authority.SimpleGrantedAuthority

fun User.toLocalCurrentUser() = LocalCurrentUser(
    user = this.toOutputUser(),
    password = this.password,
    authorities = listOf(SimpleGrantedAuthority("ROLE_${this.userRole.name}"))
)

fun User.toOutputUser() = OutputUser(
    id = this.id,
    email = this.email,
    name = this.name,
    surname = this.surname,
    userRole = this.userRole.toOutputUserRole()
)


