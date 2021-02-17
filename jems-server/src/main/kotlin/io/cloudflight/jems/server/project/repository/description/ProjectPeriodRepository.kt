package io.cloudflight.jems.server.project.repository.description

import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPeriodRepository : JpaRepository<ProjectPeriodEntity, ProjectPeriodId>
