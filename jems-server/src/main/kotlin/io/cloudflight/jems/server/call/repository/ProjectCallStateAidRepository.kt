package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.server.call.entity.ProjectCallStateAidEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectCallStateAidRepository: JpaRepository<ProjectCallStateAidEntity, Long> {
    @Query("SELECT e FROM #{#entityName} e  where e.setupId.call.id=:callId")
    fun findAllByIdCallId(callId: Long): MutableSet<ProjectCallStateAidEntity>

    fun deleteAllBySetupIdStateAidId(stateAidId: Long)
}
