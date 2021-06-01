package io.cloudflight.jems.server.user.entity

import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "account_role_permission")
class UserRolePermissionEntity(

    @EmbeddedId
    val id: UserRolePermissionId,
)
