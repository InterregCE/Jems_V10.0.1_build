package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectAssociatedOrganization
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.Optional

interface ProjectAssociatedOrganizationRepository : PagingAndSortingRepository<ProjectAssociatedOrganization, Long> {

    override fun findById(id: Long): Optional<ProjectAssociatedOrganization>

    fun findFirstByProjectIdAndId(projectId: Long, id: Long): Optional<ProjectAssociatedOrganization>

    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<ProjectAssociatedOrganization>

    fun findAllByProjectId(projectId: Long, sort: Sort): Iterable<ProjectAssociatedOrganization>
}
