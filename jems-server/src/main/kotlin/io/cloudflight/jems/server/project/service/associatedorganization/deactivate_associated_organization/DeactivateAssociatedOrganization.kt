package io.cloudflight.jems.server.project.service.associatedorganization.deactivate_associated_organization

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectForm
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.associatedorganization.AssociatedOrganizationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeactivateAssociatedOrganization(
    private val persistence: AssociatedOrganizationPersistence,
    private val projectPersistence: ProjectPersistence
) : DeactivateAssociatedOrganizationInteractor {

    @CanUpdateProjectForm
    @Transactional
    @ExceptionWrapper(DeactivateAssociatedOrganizationException::class)
    override fun deactivate(projectId: Long, id: Long) =
        ifCanBeDeactivated(projectId).run {
            persistence.deactivate(id)
        }

    fun ifCanBeDeactivated(projectId: Long) {
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            if (!projectSummary.status.isModifiableStatusAfterApproved()) {
                throw AssociatedOrganizationCannotBeDeactivatedException()
            }
        }
    }
}
