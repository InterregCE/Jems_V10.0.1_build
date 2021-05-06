package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganization
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ProjectAssociatedOrganizationRepository : JpaRepository<ProjectAssociatedOrganization, Long> {

    override fun findById(id: Long): Optional<ProjectAssociatedOrganization> {
        throw UnsupportedOperationException("use findFirstByProjectIdAndId")
    }

    override fun deleteById(id: Long) {
        throw UnsupportedOperationException("use delete with findFirstByProjectIdAndId")
    }

    fun findFirstByProjectIdAndId(projectId: Long, id: Long): Optional<ProjectAssociatedOrganization>

    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<ProjectAssociatedOrganization>

    fun findAllByProjectId(projectId: Long, sort: Sort): Iterable<ProjectAssociatedOrganization>

    fun findAllByProjectId(projectId: Long): Iterable<ProjectAssociatedOrganization>
}
