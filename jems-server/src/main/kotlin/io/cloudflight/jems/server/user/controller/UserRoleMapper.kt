package io.cloudflight.jems.server.user.controller

import io.cloudflight.jems.api.user.dto.UserRoleCreateDTO
import io.cloudflight.jems.api.user.dto.UserRoleDTO
import io.cloudflight.jems.api.user.dto.UserRolePermissionDTO
import io.cloudflight.jems.api.user.dto.UserRoleSummaryDTO
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleCreate
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import org.springframework.data.domain.Page

fun UserRoleSummary.toDto() = UserRoleSummaryDTO(
    id = id,
    name = name,
    defaultForRegisteredUser = isDefault
)

fun UserRole.toDto() = UserRoleDTO(
    id = id,
    name = name,
    defaultForRegisteredUser = isDefault,
    permissions = permissions.sorted().map { UserRolePermissionDTO.valueOf(it.name) }
)

fun Page<UserRoleSummary>.toDto() = map { it.toDto() }

fun UserRoleDTO.toModel() = UserRole(
    id = id!!,
    name = name,
    isDefault = defaultForRegisteredUser ?: false,
    permissions = permissions.toModel()
)

fun UserRoleCreateDTO.toCreateModel() = UserRoleCreate(
    name = name,
    isDefault = defaultForRegisteredUser,
    permissions = permissions.toModel(),
)

fun Collection<UserRolePermissionDTO>.toModel() = mapTo(HashSet()) { UserRolePermission.valueOf(it.name) }
