package io.cloudflight.ems.user.service

import io.cloudflight.ems.api.user.dto.InputUserCreate
import io.cloudflight.ems.api.user.dto.InputUserRegistration
import io.cloudflight.ems.api.user.dto.OutputUser
import io.cloudflight.ems.api.user.dto.OutputUserWithRole
import io.cloudflight.ems.dto.UserWithCredentials
import io.cloudflight.ems.user.entity.User
import io.cloudflight.ems.user.entity.UserRole
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
