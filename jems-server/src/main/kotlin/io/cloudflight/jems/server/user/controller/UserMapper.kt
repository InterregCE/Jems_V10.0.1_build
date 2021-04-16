package io.cloudflight.jems.server.user.controller

import io.cloudflight.jems.api.user.dto.PasswordDTO
import io.cloudflight.jems.api.user.dto.UserChangeDTO
import io.cloudflight.jems.api.user.dto.UserDTO
import io.cloudflight.jems.api.user.dto.UserRegistrationDTO
import io.cloudflight.jems.api.user.dto.UserRoleDTO
import io.cloudflight.jems.api.user.dto.UserRolePermissionDTO
import io.cloudflight.jems.api.user.dto.UserRoleSummaryDTO
import io.cloudflight.jems.api.user.dto.UserSummaryDTO
import io.cloudflight.jems.server.user.service.model.Password
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserRegistration
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.data.domain.Page


fun UserChangeDTO.toModel() = UserChange(
    id = id ?: 0,
    email = email,
    name = name,
    surname = surname,
    userRoleId = userRoleId,
)

fun UserRegistrationDTO.toModel() = UserRegistration(
    email = email,
    name = name,
    surname = surname,
    password = password,
)

fun Page<UserSummary>.toDto() = map {
    UserSummaryDTO(
        id = it.id,
        email = it.email,
        name = it.name,
        surname = it.surname,
        userRole = it.userRole.toDto(),
    )
}

fun User.toDto() = UserDTO(
    id = id,
    email = email,
    name = name,
    surname = surname,
    userRole = userRole.toDto(),
)

fun PasswordDTO.toModel() = Password(
    password = password,
    oldPassword = oldPassword,
)
