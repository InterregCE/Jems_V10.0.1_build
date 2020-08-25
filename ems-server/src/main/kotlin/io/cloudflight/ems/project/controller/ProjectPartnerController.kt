package io.cloudflight.ems.project.controller

import io.cloudflight.ems.api.project.ProjectPartnerApi
import io.cloudflight.ems.api.project.dto.InputProjectPartner
import io.cloudflight.ems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.ems.api.project.dto.OutputProjectPartner
import io.cloudflight.ems.project.service.ProjectPartnerService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerController(
    private val projectPartnerService: ProjectPartnerService
) : ProjectPartnerApi {

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getProjectPartners(projectId: Long, pageable: Pageable): Page<OutputProjectPartner> {
        return projectPartnerService.findAllByProjectId(projectId, pageable)
    }

    @PreAuthorize("@projectAuthorization.canReadProject(#projectId)")
    override fun getProjectPartnerById(projectId: Long, id: Long): OutputProjectPartner {
        return projectPartnerService.getById(id)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateProjectPartner(projectId: Long, projectPartner: InputProjectPartnerUpdate): OutputProjectPartner {
        return projectPartnerService.update(projectId, projectPartner)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun createProjectPartner(projectId: Long, projectPartner: InputProjectPartner): OutputProjectPartner {
        return projectPartnerService.createProjectPartner(projectId = projectId, projectPartner = projectPartner)
    }
}
