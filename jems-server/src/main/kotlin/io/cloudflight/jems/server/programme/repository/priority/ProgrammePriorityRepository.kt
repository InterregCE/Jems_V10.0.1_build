package io.cloudflight.jems.server.programme.repository.priority

import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammePriorityRepository : JpaRepository<ProgrammePriorityEntity, Long> {

    // max = ProgrammeObjectivePolicy enum size
    fun findTop56ByOrderByCodeAsc(): Iterable<ProgrammePriorityEntity>

    fun findFirstByCode(code: String): ProgrammePriorityEntity?
}
