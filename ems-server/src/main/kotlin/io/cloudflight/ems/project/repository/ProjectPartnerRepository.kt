package io.cloudflight.ems.project.repository

import io.cloudflight.ems.project.entity.ProjectPartner
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerRepository : PagingAndSortingRepository<ProjectPartner, Long> {

    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<ProjectPartner>

    fun findOneById(id: Long): ProjectPartner?

}
