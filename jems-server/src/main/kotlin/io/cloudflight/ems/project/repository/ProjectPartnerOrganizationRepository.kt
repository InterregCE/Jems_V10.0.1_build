package io.cloudflight.ems.project.repository

import io.cloudflight.ems.project.entity.ProjectPartnerOrganization
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerOrganizationRepository : PagingAndSortingRepository<ProjectPartnerOrganization, Long>
