package io.cloudflight.jems.server.user.repository.userrole

import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionId
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleCreate
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import org.springframework.data.domain.Page

fun UserRoleEntity.toModel(permissions: Set<UserRolePermission>, defaultUserRoleId: Long?) = UserRole(
    id = id,
    name = name,
    isDefault = defaultUserRoleId != null && defaultUserRoleId == id,
    permissions = permissions
)

fun UserRoleEntity.toModel(defaultUserRoleId: Long?) = UserRoleSummary(
    id = id,
    name = name,
    isDefault = defaultUserRoleId != null && defaultUserRoleId == id,
)

fun Page<UserRoleEntity>.toModel(defaultUserRoleId: Long?) = map { it.toModel(defaultUserRoleId) }

fun Iterable<UserRolePermissionEntity>.toModel() = mapTo(HashSet()) { it.id.permission }

fun UserRole.toEntity() = UserRoleEntity(
    id = id,
    name = name
)

fun UserRoleCreate.toEntity() = UserRoleEntity(
    id = 0L,
    name = name
)

fun Set<UserRolePermission>.toEntity(roleEntity: UserRoleEntity) = map {
    UserRolePermissionEntity(
        id = UserRolePermissionId(
            userRole = roleEntity,
            permission = it
        )
    )
}
