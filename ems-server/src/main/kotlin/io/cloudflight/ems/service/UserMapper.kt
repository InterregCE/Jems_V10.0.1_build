package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.security.model.LocalCurrentUser
import org.springframework.security.core.authority.SimpleGrantedAuthority

fun Account.toLocalCurrentUser() = LocalCurrentUser(
    user = this.toOutputUser(),
    password = this.password,
    authorities = listOf(SimpleGrantedAuthority("ROLE_${this.accountRole.name}"))
)

fun Account.toOutputUser() = OutputUser(
    id = this.id,
    email = this.email,
    name = this.name,
    surname = this.surname,
    userRole = this.accountRole.toOutputUserRole()
)


