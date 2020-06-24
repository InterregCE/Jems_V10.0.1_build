package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputAccount
import io.cloudflight.ems.api.dto.OutputAccount
import io.cloudflight.ems.dto.UserWithCredentials
import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.entity.AccountRole
import io.cloudflight.ems.security.model.LocalCurrentUser
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

fun UserWithCredentials.toLocalCurrentUser() = LocalCurrentUser(
    user = this.user,
    password = this.password,
    authorities = listOf(SimpleGrantedAuthority("ROLE_${this.user.accountRole.name}"))
)

fun Account.toOutputUser() = OutputAccount(
    id = this.id,
    email = this.email,
    name = this.name,
    surname = this.surname,
    accountRole = this.accountRole.toOutputAccountRole()
)

fun InputAccount.toEntity(role: AccountRole) = Account(
    id = null,
    email = this.email,
    name = this.name,
    surname = this.surname,
    accountRole = role,
    password = BCryptPasswordEncoder().encode(this.email)
)
