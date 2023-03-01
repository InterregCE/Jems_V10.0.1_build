package io.cloudflight.jems.server.project.service.contracting.sectionLock.getLockedSections

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.contracting.sectionLock.ProjectContractingSectionLockPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetLockedSections(
    private val projectContractingSectionLockPersistence: ProjectContractingSectionLockPersistence,
): GetLockedSectionsInteractor {

    @ExceptionWrapper(ProjectContractingGetLockedSectionsException::class)
    @Transactional(readOnly = true)
    override fun getLockedSections(projectId: Long): List<ProjectContractingSection> {
        return projectContractingSectionLockPersistence.getLockedSections(projectId)
    }

}