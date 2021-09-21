package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectAssociatedOrganizationApi
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectForm
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.associatedorganization.get_associated_organization.GetAssociatedOrganizationInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectAssociatedOrganizationController(
    private val associatedOrganizationService: ProjectAssociatedOrganizationService,
    private val getAssociatedOrganizationInteractor: GetAssociatedOrganizationInteractor
) : ProjectAssociatedOrganizationApi {

    @CanRetrieveProjectForm
    override fun getAssociatedOrganizations(
        projectId: Long,
        pageable: Pageable,
        version: String?
    ): Page<OutputProjectAssociatedOrganization> {
        return getAssociatedOrganizationInteractor.findAllByProjectId(projectId, pageable, version)
    }

    @CanRetrieveProjectForm
    override fun getAssociatedOrganizationById(
        projectId: Long,
        id: Long,
        version: String?
    ): OutputProjectAssociatedOrganizationDetail? {
        return getAssociatedOrganizationInteractor.getById(projectId, id, version)
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
}
