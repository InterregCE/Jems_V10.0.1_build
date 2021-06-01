package io.cloudflight.jems.server.user.repository.userrole

import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionId
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleCreate
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import org.springframework.data.domain.Page

fun UserRoleEntity.toModel(permissions: Set<UserRolePermission>) = UserRole(
    id = id,
    name = name,
    permissions = permissions,
)

fun UserRoleEntity.toModel() = UserRoleSummary(
    id = id,
    name = name,
)

fun Page<UserRoleEntity>.toModel() = map { it.toModel() }

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
