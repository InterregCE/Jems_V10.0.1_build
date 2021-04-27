package io.cloudflight.jems.server.user.entity

import io.cloudflight.jems.server.user.service.model.UserRolePermission
import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class UserRolePermissionId(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_role_id")
    @field:NotNull
    val userRole: UserRoleEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var permission: UserRolePermission,
) : Serializable {

    override fun equals(other: Any?) = this === other ||
        other is UserRolePermissionId &&
        userRole.id == other.userRole.id &&
        permission == other.permission

    override fun hashCode() = Objects.hash(userRole.id, permission)
}
