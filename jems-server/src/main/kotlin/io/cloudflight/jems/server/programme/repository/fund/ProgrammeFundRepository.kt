package io.cloudflight.jems.server.programme.repository.fund

import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeFundRepository : JpaRepository<ProgrammeFundEntity, Long> {

    @EntityGraph(value = "ProgrammeFundEntity.fetchWithTranslations")
    fun findTop20ByOrderById(): Iterable<ProgrammeFundEntity>

    fun getTop20ByIdInAndSelectedTrue(ids: Set<Long>): Iterable<ProgrammeFundEntity>

    @Query(
        nativeQuery = true,
        value = "SELECT DISTINCT programme_fund FROM project_call_fund"
    )
    fun getFundsAlreadyInUse(): Iterable<Long>


    fun getAllBySelectedTrue(): List<ProgrammeFundEntity>

}
