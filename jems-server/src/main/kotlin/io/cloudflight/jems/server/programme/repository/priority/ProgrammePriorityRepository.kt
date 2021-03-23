package io.cloudflight.jems.server.programme.repository.priority

import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammePriorityRepository : PagingAndSortingRepository<ProgrammePriorityEntity, Long> {

    // max = ProgrammeObjectivePolicy enum size
    fun findTop56ByOrderByCodeAsc(): Iterable<ProgrammePriorityEntity>

    fun findFirstByCode(code: String): ProgrammePriorityEntity?

}
