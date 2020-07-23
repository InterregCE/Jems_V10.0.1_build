package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.ProgrammePriority
import io.cloudflight.ems.entity.ProjectFile
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProgrammePriorityRepository : PagingAndSortingRepository<ProgrammePriority, Long> {

    fun findFirstByCode(code: String): ProgrammePriority?

    fun findFirstByTitle(title: String): ProgrammePriority?

}
