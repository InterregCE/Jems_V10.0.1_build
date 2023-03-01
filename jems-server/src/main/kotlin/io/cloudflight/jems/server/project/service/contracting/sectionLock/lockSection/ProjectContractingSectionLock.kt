package io.cloudflight.jems.server.project.service.contracting.sectionLock.lockSection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.contracting.sectionLock.ProjectContractingSectionLockPersistence
import io.cloudflight.jems.server.project.service.projectContractingSectionLocked
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectContractingSectionLock(
    private val projectContractingSectionLockPersistence: ProjectContractingSectionLockPersistence,
    private val auditPublisher: ApplicationEventPublisher
) : ProjectContractingSectionLockInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(ProjectContractingSectionLockException::class)
    override fun lock(section: ProjectContractingSection, projectId: Long) {
        if (!projectContractingSectionLockPersistence.isLocked(projectId,section)) {
            return projectContractingSectionLockPersistence.lock(projectId,section).also {
                auditPublisher.publishEvent(
                    projectContractingSectionLocked(
                        context = this,
                        contractingSection = section,
                        projectId = projectId
                    )
                )
            }
        }
    }

}