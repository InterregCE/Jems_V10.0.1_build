package io.cloudflight.jems.server.user.repository.userrole

import io.cloudflight.jems.server.user.entity.UserRolePermissionEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionId
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRolePermissionRepository : JpaRepository<UserRolePermissionEntity, UserRolePermissionId> {

    fun findAllByIdUserRoleId(userRoleId: Long): Iterable<UserRolePermissionEntity>

    @Query(
        """
            SELECT e.id.userRole.id
            FROM #{#entityName} e
            GROUP BY e.id.userRole.id
            HAVING
                SUM(CASE WHEN e.id.permission IN :needsToHaveAtLeastOneFrom THEN 1 ELSE 0 END) > 0
                    AND
                SUM(CASE WHEN e.id.permission IN :needsNotToHaveAnyOf THEN 1 ELSE 0 END) = 0
        """,
    )
    fun findRoleIdsHavingAndNotHavingPermissions(
        @Param("needsToHaveAtLeastOneFrom") needsToHaveAtLeastOneFrom: Collection<UserRolePermission>,
        @Param("needsNotToHaveAnyOf") needsNotToHaveAnyOf: Collection<UserRolePermission>
    ): Set<Long>

}
