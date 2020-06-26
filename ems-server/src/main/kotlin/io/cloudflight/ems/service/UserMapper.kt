package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputUser
import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.dto.UserWithCredentials
import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.entity.AccountRole
import io.cloudflight.ems.security.model.LocalCurrentUser
import org.springframework.security.core.authority.SimpleGrantedAuthority

fun UserWithCredentials.toLocalCurrentUser() = LocalCurrentUser(
    user = this.user,
    password = this.password,
    authorities = listOf(SimpleGrantedAuthority("ROLE_${this.user.userRole.name}"))
)

fun Account.toOutputUser() = OutputUser(
    id = this.id,
    email = this.email,
    name = this.name,
    surname = this.surname,
    userRole = this.accountRole.toOutputUserRole()
)

fun InputUser.toEntity(role: AccountRole, password: String) = Account(
    id = null,
    email = this.email,
    name = this.name,
    surname = this.surname,
    accountRole = role,
    password = password
)
