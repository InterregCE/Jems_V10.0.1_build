package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.ProjectStatus
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectStatusRepository : PagingAndSortingRepository<ProjectStatus, Long>
