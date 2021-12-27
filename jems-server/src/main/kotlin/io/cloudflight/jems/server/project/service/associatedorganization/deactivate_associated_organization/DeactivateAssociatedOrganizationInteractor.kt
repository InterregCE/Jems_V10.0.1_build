package io.cloudflight.jems.server.project.service.associatedorganization.deactivate_associated_organization

interface DeactivateAssociatedOrganizationInteractor {
    fun deactivate(projectId: Long, id: Long)
}
