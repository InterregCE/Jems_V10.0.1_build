package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectAssociatedOrganizationApi
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationCreate
import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationUpdate
import io.cloudflight.jems.server.project.service.ProjectAssociatedOrganizationService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectAssociatedOrganizationController(
    private val associatedOrganizationService: ProjectAssociatedOrganizationService
) : ProjectAssociatedOrganizationApi{
    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getAssociatedOrganizations(projectId: Long, pageable: Pageable): Page<OutputProjectAssociatedOrganization> {
        return associatedOrganizationService.findAllByProjectId(projectId, pageable)
    }

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getAssociatedOrganizationById(projectId: Long, id: Long): OutputProjectAssociatedOrganizationDetail {
        return associatedOrganizationService.getById(projectId, id)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun createAssociatedOrganization(projectId: Long, associatedOrganization: InputProjectAssociatedOrganizationCreate): OutputProjectAssociatedOrganizationDetail {
        return associatedOrganizationService.create(projectId = projectId, associatedOrganization = associatedOrganization)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateAssociatedOrganization(projectId: Long, associatedOrganization: InputProjectAssociatedOrganizationUpdate): OutputProjectAssociatedOrganizationDetail {
        return associatedOrganizationService.update(projectId, associatedOrganization)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun deleteAssociatedOrganization(projectId: Long, id: Long) {
        return associatedOrganizationService.delete(projectId, id);
    }
}
