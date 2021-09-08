package io.cloudflight.jems.server.user.service.model

class UserRolePermissionNode(
    val name: String,
    val viewPermissions: Set<UserRolePermission> = emptySet(),
    val editPermissions: Set<UserRolePermission> = emptySet(),
    val children: List<UserRolePermissionNode> = emptyList(),
    val type: UserRolePermissionNodeType
)
