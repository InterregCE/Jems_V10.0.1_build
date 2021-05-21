package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectAssociatedOrganizationApi
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationCreate
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationUpdate
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
) : ProjectAssociatedOrganizationApi{
    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getAssociatedOrganizations(projectId: Long, pageable: Pageable, version: String?): Page<OutputProjectAssociatedOrganization> {
        return getAssociatedOrganizationInteractor.findAllByProjectId(projectId, pageable, version)
    }

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getAssociatedOrganizationById(projectId: Long, id: Long, version: String?): OutputProjectAssociatedOrganizationDetail {
        return getAssociatedOrganizationInteractor.getById(projectId, id, version)
    }

    @PreAuthorize("@projectAuthorization.canOwnerUpdateProject(#projectId)")
    override fun createAssociatedOrganization(projectId: Long, associatedOrganization: InputProjectAssociatedOrganizationCreate): OutputProjectAssociatedOrganizationDetail {
        return associatedOrganizationService.create(projectId = projectId, associatedOrganization = associatedOrganization)
    }

    @PreAuthorize("@projectAuthorization.canOwnerUpdateProject(#projectId)")
    override fun updateAssociatedOrganization(projectId: Long, associatedOrganization: InputProjectAssociatedOrganizationUpdate): OutputProjectAssociatedOrganizationDetail {
        return associatedOrganizationService.update(projectId, associatedOrganization)
    }

    @PreAuthorize("@projectAuthorization.canOwnerUpdateProject(#projectId)")
    override fun deleteAssociatedOrganization(projectId: Long, id: Long) {
        return associatedOrganizationService.delete(projectId, id);
    }
}
