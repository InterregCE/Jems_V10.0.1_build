package io.cloudflight.jems.server.programme.repository.checklist

import io.cloudflight.jems.server.programme.entity.checklist.ProgrammeChecklistEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeChecklistRepository : JpaRepository<ProgrammeChecklistEntity, Long> {

    fun findTop100ByOrderById(): Iterable<ProgrammeChecklistEntity>
}
