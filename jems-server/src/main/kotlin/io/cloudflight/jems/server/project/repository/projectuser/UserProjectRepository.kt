package io.cloudflight.jems.server.project.repository.projectuser

import io.cloudflight.jems.server.project.entity.projectuser.UserProjectEntity
import io.cloudflight.jems.server.project.entity.projectuser.UserProjectId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserProjectRepository : JpaRepository<UserProjectEntity, UserProjectId> {

    @Query("SELECT e.id.projectId FROM #{#entityName} e where e.id.userId=:userId")
    fun findProjectIdsForUserId(userId: Long): Set<Long>

    @Query("SELECT e.id.userId FROM #{#entityName} e where e.id.projectId=:projectId")
    fun findUserIdsForProjectId(projectId: Long): Set<Long>

    fun deleteAllByIdIn(id: Collection<UserProjectId>)

    fun deleteAllByIdUserId(id: Long)
}
