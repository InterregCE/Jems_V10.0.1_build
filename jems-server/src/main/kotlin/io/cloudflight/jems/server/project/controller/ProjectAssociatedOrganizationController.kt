package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectAssociatedOrganizationApi
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectForm
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.associatedorganization.deactivate_associated_organization.DeactivateAssociatedOrganizationInteractor
import io.cloudflight.jems.server.project.service.associatedorganization.get_associated_organization.GetAssociatedOrganizationInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectAssociatedOrganizationController(
    private val associatedOrganizationService: ProjectAssociatedOrganizationService,
    private val getAssociatedOrganization: GetAssociatedOrganizationInteractor,
    private val deactivateAssociatedOrganization: DeactivateAssociatedOrganizationInteractor
) : ProjectAssociatedOrganizationApi {

    @CanRetrieveProjectForm
    override fun getAssociatedOrganizations(
        projectId: Long,
        pageable: Pageable,
        version: String?
    ): Page<OutputProjectAssociatedOrganization> {
        return getAssociatedOrganization.findAllByProjectId(projectId, pageable, version)
    }

    @CanRetrieveProjectForm
    override fun getAssociatedOrganizationById(
        projectId: Long,
        id: Long,
        version: String?
    ): OutputProjectAssociatedOrganizationDetail? {
        return getAssociatedOrganization.getById(projectId, id, version)
    }

    @CanUpdateProjectForm
    override fun createAssociatedOrganization(
        projectId: Long,
        associatedOrganization: InputProjectAssociatedOrganization
    ): OutputProjectAssociatedOrganizationDetail {
        return associatedOrganizationService.create(
            projectId = projectId,
            associatedOrganization = associatedOrganization
        )
    }

    @CanUpdateProjectForm
    override fun updateAssociatedOrganization(
        projectId: Long,
        associatedOrganization: InputProjectAssociatedOrganization
    ): OutputProjectAssociatedOrganizationDetail {
        return associatedOrganizationService.update(projectId, associatedOrganization)
    }

    @CanUpdateProjectForm
    override fun deleteAssociatedOrganization(projectId: Long, id: Long) {
        return associatedOrganizationService.delete(projectId, id);
    }

    override fun deactivateAssociatedOrganization(projectId: Long, id: Long) {
        deactivateAssociatedOrganization.deactivate(projectId, id)
    }

}
