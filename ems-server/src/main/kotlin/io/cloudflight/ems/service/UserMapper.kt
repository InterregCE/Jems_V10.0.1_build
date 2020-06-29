package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.user.InputUserCreate
import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.OutputUser
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

fun InputUserCreate.toEntity(role: AccountRole, password: String) = Account(
    id = null,
    email = this.email,
    name = this.name,
    surname = this.surname,
    accountRole = role,
    password = password
)

fun InputUserRegistration.toEntity(role: AccountRole, password: String) = Account(
    id = null,
    email = this.email,
    name = this.name,
    surname = this.surname,
    accountRole = role,
    password = password
)
