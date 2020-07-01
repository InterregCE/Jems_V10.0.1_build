package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.Project
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : PagingAndSortingRepository<Project, Long> {

    fun findAllByApplicant_Id(applicantId: Long, pageable: Pageable): Page<Project>

    fun findOneById(id: Long): Project?

}
