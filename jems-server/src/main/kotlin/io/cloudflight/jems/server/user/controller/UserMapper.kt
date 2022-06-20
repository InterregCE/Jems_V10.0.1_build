package io.cloudflight.jems.server.user.controller

import io.cloudflight.jems.api.user.dto.*
import io.cloudflight.jems.server.captcha.Captcha
import io.cloudflight.jems.server.user.service.model.Password
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserRegistration
import io.cloudflight.jems.server.user.service.model.UserSearchRequest
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.data.domain.Page

fun UserChangeDTO.toModel() = UserChange(
    id = id ?: 0,
    email = email,
    name = name,
    surname = surname,
    userRoleId = userRoleId,
    userStatus = userStatus.toModel()
)

fun UserSearchRequestDTO.toModel() = UserSearchRequest(
    name = name,
    email = email,
    surname = surname,
    roles = roles,
    userStatuses = userStatuses.map { it.toModel() }.toSet()
)

fun UserRegistrationDTO.toModel() = UserRegistration(
    email = email,
    name = name,
    surname = surname,
    password = password,
    captcha = captcha ?: "",
)

fun UserSummary.toSummaryDto() = UserSummaryDTO(
    id = id,
    email = email,
    name = name,
    surname = surname,
    userRole = userRole.toDto(),
    userStatus = userStatus.toDto()
)

fun User.toDto() = UserDTO(
    id = id,
    email = email,
    name = name,
    surname = surname,
    userRole = userRole.toDto(),
    userStatus = userStatus.toDto(),
)

fun UserSummary.toDto() = OutputUser(
    id = this.id,
    email = this.email,
    name = this.name,
    surname = this.surname
)

fun PasswordDTO.toModel() = Password(
    password = password,
    oldPassword = oldPassword,
)

fun UserStatusDTO.toModel() = UserStatus.valueOf(name)

fun UserStatus.toDto() = UserStatusDTO.valueOf(name)

fun Captcha.toDto() = CaptchaDTO(
    captcha, realCaptcha
)
