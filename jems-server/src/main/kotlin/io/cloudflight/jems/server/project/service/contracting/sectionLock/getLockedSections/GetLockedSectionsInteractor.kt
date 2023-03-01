package io.cloudflight.jems.server.project.service.contracting.sectionLock.getLockedSections

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection

interface GetLockedSectionsInteractor {

    fun getLockedSections(projectId: Long): List<ProjectContractingSection>
}