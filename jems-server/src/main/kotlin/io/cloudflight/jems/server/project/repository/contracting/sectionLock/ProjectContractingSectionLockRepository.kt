package io.cloudflight.jems.server.project.repository.contracting.sectionLock

import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingSectionLockEntity
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingSectionLockId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectContractingSectionLockRepository: JpaRepository<ProjectContractingSectionLockEntity, ProjectContractingSectionLockId> {

    fun findAllByContractingSectionLockIdProjectId(projectId: Long): List<ProjectContractingSectionLockEntity>
}