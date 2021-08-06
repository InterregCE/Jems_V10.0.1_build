package io.cloudflight.jems.server.programme.repository.stateaid

import io.cloudflight.jems.server.programme.entity.stateaid.ProgrammeStateAidEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeStateAidRepository : JpaRepository<ProgrammeStateAidEntity, Long> {

    @EntityGraph(value = "ProgrammeStateAidEntity.fetchWithTranslations")
    fun findTop20ByOrderById(): Iterable<ProgrammeStateAidEntity>

    @EntityGraph(value = "ProgrammeStateAidEntity.fetchWithTranslations")
    override fun findAllById(ids: Iterable<Long>): List<ProgrammeStateAidEntity>

}
