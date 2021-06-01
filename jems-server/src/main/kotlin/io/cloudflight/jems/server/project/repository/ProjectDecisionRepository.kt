package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.project.entity.ProjectDecisionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectDecisionRepository: JpaRepository<ProjectDecisionEntity, Long>