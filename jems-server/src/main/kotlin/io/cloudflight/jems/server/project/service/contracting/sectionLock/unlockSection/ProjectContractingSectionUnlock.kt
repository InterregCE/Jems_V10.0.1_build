package io.cloudflight.jems.server.project.service.contracting.sectionLock.unlockSection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.contracting.sectionLock.ProjectContractingSectionLockPersistence
import io.cloudflight.jems.server.project.service.projectContractingSectionUnlocked
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectContractingSectionUnlock(
    private val projectContractingSectionLockPersistence: ProjectContractingSectionLockPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : ProjectContractingSectionUnlockInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(ProjectContractingSectionUnlockException::class)
    override fun unlock(section: ProjectContractingSection, projectId: Long) {
        if (projectContractingSectionLockPersistence.isLocked(projectId,section)) {
            return projectContractingSectionLockPersistence.unlock(projectId,section).also {
                auditPublisher.publishEvent(
                    projectContractingSectionUnlocked(
                        context = this,
                        contractingSection = section,
                        projectId = projectId
                    )
                )
            }
        }
    }
}