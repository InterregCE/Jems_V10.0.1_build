package io.cloudflight.jems.server.project.service.contracting.sectionLock

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection


interface ProjectContractingSectionLockPersistence {
    fun isLocked(projectId: Long, section: ProjectContractingSection): Boolean
    fun getLockedSections(projectId: Long): List<ProjectContractingSection>
    fun lock(projectId: Long, section: ProjectContractingSection)
    fun unlock(projectId: Long, section: ProjectContractingSection)

}