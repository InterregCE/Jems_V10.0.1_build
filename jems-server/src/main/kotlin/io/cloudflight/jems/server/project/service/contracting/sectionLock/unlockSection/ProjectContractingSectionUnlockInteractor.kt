package io.cloudflight.jems.server.project.service.contracting.sectionLock.unlockSection

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection

interface ProjectContractingSectionUnlockInteractor {

    fun unlock(section: ProjectContractingSection, projectId: Long)
}