package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.server.programme.entity.ProgrammePriority
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammePriorityRepository : PagingAndSortingRepository<ProgrammePriority, Long> {

    fun findFirstByCode(code: String): ProgrammePriority?

    fun findFirstByTitle(title: String): ProgrammePriority?

}
