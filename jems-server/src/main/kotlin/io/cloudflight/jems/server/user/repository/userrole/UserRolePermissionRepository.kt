package io.cloudflight.jems.server.user.repository.userrole

import io.cloudflight.jems.server.user.entity.UserRolePermissionEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRolePermissionRepository : JpaRepository<UserRolePermissionEntity, UserRolePermissionId> {

    fun findAllByIdUserRoleId(userRoleId: Long): Iterable<UserRolePermissionEntity>

}
