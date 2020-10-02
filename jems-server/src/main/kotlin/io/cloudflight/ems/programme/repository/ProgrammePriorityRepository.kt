package io.cloudflight.ems.programme.repository

import io.cloudflight.ems.programme.entity.ProgrammePriority
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammePriorityRepository : PagingAndSortingRepository<ProgrammePriority, Long> {

    fun findFirstByCode(code: String): ProgrammePriority?

    fun findFirstByTitle(title: String): ProgrammePriority?

}
