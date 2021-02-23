package io.cloudflight.jems.server.programme.repository.legalstatus

import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeLegalStatusRepository : JpaRepository<ProgrammeLegalStatusEntity, Long> {

    @EntityGraph(value = "ProgrammeLegalStatusEntity.fetchWithTranslations")
    fun findTop20ByOrderById(): Iterable<ProgrammeLegalStatusEntity>

    @EntityGraph(value = "ProgrammeLegalStatusEntity.fetchWithTranslations")
    override fun findAllById(ids: Iterable<Long>): List<ProgrammeLegalStatusEntity>

}
