package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeLumpSumRepository : JpaRepository<ProgrammeLumpSumEntity, Long> {

    fun findTop100ByOrderById(): Iterable<ProgrammeLumpSumEntity>
    fun findAllByIdIn(ids: List<Long>): Iterable<ProgrammeLumpSumEntity>

    @Query(
        nativeQuery = true,
        value = "SELECT COUNT(programme_lump_sum_id) from project_call_lump_sum WHERE programme_lump_sum_id = :lumpSumId"
    )
    fun getNumberOfOccurrencesInCalls(lumpSumId: Long): Int

}
