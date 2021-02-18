package io.cloudflight.jems.server.project.repository.result

import io.cloudflight.jems.server.project.entity.result.ProjectResultEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectResultRepository : CrudRepository<ProjectResultEntity, Long>
