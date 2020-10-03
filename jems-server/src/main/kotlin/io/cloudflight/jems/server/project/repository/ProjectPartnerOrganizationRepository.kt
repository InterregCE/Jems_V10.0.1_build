package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectPartnerOrganization
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerOrganizationRepository : PagingAndSortingRepository<ProjectPartnerOrganization, Long>
