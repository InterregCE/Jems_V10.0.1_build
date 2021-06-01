package io.cloudflight.jems.server.programme.repository.costoption

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeLumpSumRepository : JpaRepository<ProgrammeLumpSumEntity, Long> {

    fun findTop100ByOrderById(): Iterable<ProgrammeLumpSumEntity>
    fun findAllByIdIn(ids: List<Long>): Iterable<ProgrammeLumpSumEntity>

}
