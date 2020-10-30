package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectPartnerApi
import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.InputProjectPartnerContribution
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerAddress
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.cofinancing.InputProjectPartnerCoFinancingWrapper
import io.cloudflight.jems.server.project.authorization.CanReadProject
import io.cloudflight.jems.server.project.authorization.CanReadProjectPartner
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerService
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerController(
    private val projectPartnerService: ProjectPartnerService,
    private val projectPartnerCoFinancingService: ProjectPartnerCoFinancingService
) : ProjectPartnerApi {

    @CanReadProject
    override fun getProjectPartners(projectId: Long, pageable: Pageable): Page<OutputProjectPartner> {
        return projectPartnerService.findAllByProjectId(projectId, pageable)
    }

    @CanReadProject
    override fun getProjectPartnersForDropdown(projectId: Long, pageable: Pageable): List<OutputProjectPartner> {
        return projectPartnerService.findAllByProjectIdForDropdown(projectId, pageable.sort)
    }

    @CanReadProjectPartner
    override fun getProjectPartnerById(projectId: Long, partnerId: Long): OutputProjectPartnerDetail {
        return projectPartnerService.getById(partnerId)
    }

    @CanUpdateProject
    override fun createProjectPartner(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail {
        return projectPartnerService.create(projectId = projectId, projectPartner = projectPartner)
    }

    @PreAuthorize("@projectAuthorization.canUpdateProject(#projectId)")
    override fun updateProjectPartner(projectId: Long, projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail {
        return projectPartnerService.update(projectPartner)
    }

    @CanUpdateProjectPartner
    override fun updateProjectPartnerAddress(projectId: Long, partnerId: Long, addresses: Set<InputProjectPartnerAddress>): OutputProjectPartnerDetail {
        return projectPartnerService.updatePartnerAddresses(partnerId, addresses)
    }

    @CanUpdateProjectPartner
    override fun updateProjectPartnerContact(projectId: Long, partnerId: Long, contacts: Set<InputProjectContact>): OutputProjectPartnerDetail {
        return projectPartnerService.updatePartnerContacts(partnerId, contacts)
    }

    @CanUpdateProjectPartner
    override fun updateProjectPartnerContribution(projectId: Long, partnerId: Long, partnerContribution: InputProjectPartnerContribution): OutputProjectPartnerDetail {
        return projectPartnerService.updatePartnerContribution(partnerId, partnerContribution)
    }

    @CanUpdateProjectPartner
    override fun updateProjectPartnerCoFinancing(projectId: Long, partnerId: Long, partnerCoFinancing: InputProjectPartnerCoFinancingWrapper): OutputProjectPartnerDetail {
        return projectPartnerCoFinancingService.updatePartnerCoFinancing(partnerId, partnerCoFinancing.finances)
    }

    @CanUpdateProjectPartner
    override fun deleteProjectPartner(projectId: Long, partnerId: Long) {
        return projectPartnerService.deletePartner(partnerId)
    }

}
