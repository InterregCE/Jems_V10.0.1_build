package io.cloudflight.jems.server.project.repository.contracting.sectionLock

import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingSectionLockEntity
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingSectionLockId
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.contracting.sectionLock.ProjectContractingSectionLockPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectContractingSectionLockPersistenceProvider(
    private val projectContractingSectionLockRepository: ProjectContractingSectionLockRepository
) : ProjectContractingSectionLockPersistence {

    @Transactional(readOnly = true)
    override fun isLocked(projectId: Long, section: ProjectContractingSection): Boolean =
        projectContractingSectionLockRepository.findById(ProjectContractingSectionLockId(projectId, section)).isPresent


    @Transactional(readOnly = true)
    override fun getLockedSections(projectId: Long): List<ProjectContractingSection> =
        projectContractingSectionLockRepository.findAllByContractingSectionLockIdProjectId(projectId)
            .map { it.contractingSectionLockId.section }


    @Transactional
    override fun lock(projectId: Long, section: ProjectContractingSection) {
        projectContractingSectionLockRepository.save(
            ProjectContractingSectionLockEntity(
                ProjectContractingSectionLockId(projectId, section)
            )
        )
    }

    @Transactional
    override fun unlock(projectId: Long, section: ProjectContractingSection) {
        projectContractingSectionLockRepository.deleteById(
            ProjectContractingSectionLockId(
                projectId, section
            )
        )
    }

}
