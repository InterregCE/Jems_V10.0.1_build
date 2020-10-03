package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectPartnerApi
import io.cloudflight.jems.api.project.dto.InputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.InputProjectPartnerContribution
import io.cloudflight.jems.api.project.dto.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.OutputProjectPartnerDetail
import io.cloudflight.jems.server.project.service.ProjectPartnerService
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
    override fun getProjectPartnerById(projectId: Long, id: Long): OutputProjectPartnerDetail {
        return projectPartnerService.getById(projectId, id)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun createProjectPartner(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail {
        return projectPartnerService.create(projectId = projectId, projectPartner = projectPartner)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateProjectPartner(projectId: Long, projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail {
        return projectPartnerService.update(projectId, projectPartner)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateProjectPartnerContact(projectId: Long, id: Long, projectPartner: Set<InputProjectPartnerContact>): OutputProjectPartnerDetail {
        return projectPartnerService.updatePartnerContact(projectId, id, projectPartner)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateProjectPartnerContribution(projectId: Long, id: Long, partnerContribution: InputProjectPartnerContribution): OutputProjectPartnerDetail {
        return projectPartnerService.updatePartnerContribution(projectId, id, partnerContribution)
    }

}
