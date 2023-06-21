package io.cloudflight.jems.server.project.service.contracting.sectionLock.lockSection

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection

interface ProjectContractingSectionLockInteractor {

    fun lock(section: ProjectContractingSection, projectId: Long)
}