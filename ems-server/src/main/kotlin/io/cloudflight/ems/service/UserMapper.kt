package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.user.InputUserCreate
import io.cloudflight.ems.api.dto.user.InputUserRegistration
import io.cloudflight.ems.api.dto.user.OutputCurrentUser
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.dto.UserWithCredentials
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.UserRole
import io.cloudflight.ems.entity.AuditUser
import io.cloudflight.ems.security.model.CurrentUser
import io.cloudflight.ems.security.model.LocalCurrentUser
import org.springframework.security.core.authority.SimpleGrantedAuthority

fun UserWithCredentials.toLocalCurrentUser() = LocalCurrentUser(
    user = this.user,
    password = this.password,
    authorities = listOf(SimpleGrantedAuthority("ROLE_${this.user.userRole.name}"))
)

fun User.toOutputUser() = OutputUser(
    id = this.id,
    email = this.email,
    name = this.name,
    surname = this.surname
)

fun User.toOutputUserWithRole() = OutputUserWithRole(
    id = this.id,
    email = this.email,
    name = this.name,
    surname = this.surname,
    userRole = this.userRole.toOutputUserRole()
)

fun InputUserCreate.toEntity(role: UserRole, password: String) = User(
    id = null,
    email = this.email,
    name = this.name,
    surname = this.surname,
    userRole = role,
    password = password
)

fun InputUserRegistration.toEntity(role: UserRole, password: String) = User(
    id = null,
    email = this.email,
    name = this.name,
    surname = this.surname,
    userRole = role,
    password = password
)

fun CurrentUser.toEsUser() = AuditUser (
    id = user.id ?: 0,
    email = user.email
)

fun OutputCurrentUser.toEsUser() = AuditUser (
    id = id,
    email = name
)
