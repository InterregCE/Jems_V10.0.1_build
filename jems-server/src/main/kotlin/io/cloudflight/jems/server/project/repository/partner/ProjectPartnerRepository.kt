package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.project.entity.partner.ProjectPartner
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProjectPartnerRepository : PagingAndSortingRepository<ProjectPartner, Long> {

    @EntityGraph(attributePaths = ["contacts"])
    override fun findById(id: Long): Optional<ProjectPartner>

    @EntityGraph(attributePaths = ["contacts"])
    fun findFirstByProjectIdAndId(projectId: Long, id: Long): Optional<ProjectPartner>

    fun findAllByProjectId(projectId: Long, pageable: Pageable): Page<ProjectPartner>

    fun findAllByProjectId(projectId: Long, sort: Sort): Iterable<ProjectPartner>

    fun findFirstByProjectIdAndRole(projectId: Long, role: ProjectPartnerRole): Optional<ProjectPartner>

}
