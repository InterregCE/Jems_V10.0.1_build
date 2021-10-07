package io.cloudflight.jems.server.project.controller.partner

import io.cloudflight.jems.api.project.partner.ProjectPartnerApi
import io.cloudflight.jems.api.project.dto.ProjectContactDTO
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.ProjectPartnerStateAidDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectBudgetPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO
import io.cloudflight.jems.server.project.service.partner.create_project_partner.CreateProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.delete_project_partner.DeleteProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.get_project_partner.GetProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.state_aid.get_project_partner_state_aid.GetProjectPartnerStateAidInteractor
import io.cloudflight.jems.server.project.service.partner.update_project_partner.UpdateProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.state_aid.update_project_partner_state_aid.UpdateProjectPartnerStateAidInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerController(
    private val getProjectPartner: GetProjectPartnerInteractor,
    private val createProjectPartner: CreateProjectPartnerInteractor,
    private val updateProjectPartner: UpdateProjectPartnerInteractor,
    private val getProjectPartnerStateAid: GetProjectPartnerStateAidInteractor,
    private val updateProjectPartnerStateAid: UpdateProjectPartnerStateAidInteractor,
    private val deleteProjectPartner: DeleteProjectPartnerInteractor,
) : ProjectPartnerApi {

    override fun getProjectPartners(projectId: Long, pageable: Pageable, version: String?): Page<ProjectBudgetPartnerSummaryDTO> =
       getProjectPartner.findAllByProjectId(projectId, pageable, version).toDto()

    override fun getProjectPartnersForDropdown(projectId: Long, pageable: Pageable, version: String?): List<ProjectPartnerSummaryDTO> =
       getProjectPartner.findAllByProjectIdForDropdown(projectId, pageable.sort, version).toDto()

    override fun getProjectPartnerById(partnerId: Long, version: String?): ProjectPartnerDetailDTO =
        getProjectPartner.getById(partnerId, version).toDto()

    override fun createProjectPartner(projectId: Long, projectPartner: ProjectPartnerDTO): ProjectPartnerDetailDTO =
         createProjectPartner.create(projectId = projectId, projectPartner = projectPartner.toModel()).toDto()

    override fun updateProjectPartner(projectPartner: ProjectPartnerDTO): ProjectPartnerDetailDTO =
        updateProjectPartner.update(projectPartner.toModel()).toDto()

    override fun updateProjectPartnerAddress(partnerId: Long, addresses: Set<ProjectPartnerAddressDTO>): ProjectPartnerDetailDTO =
        updateProjectPartner.updatePartnerAddresses(partnerId, addresses.toAddressModel()).toDto()

    override fun updateProjectPartnerContact(partnerId: Long, contacts: Set<ProjectContactDTO>): ProjectPartnerDetailDTO =
        updateProjectPartner.updatePartnerContacts(partnerId, contacts.toContactModel()).toDto()

    override fun updateProjectPartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivationDTO): ProjectPartnerDetailDTO =
        updateProjectPartner.updatePartnerMotivation(partnerId, motivation.toModel()).toDto()

    override fun getProjectPartnerStateAid(partnerId: Long, version: String?): ProjectPartnerStateAidDTO =
        getProjectPartnerStateAid.getStateAidForPartnerId(partnerId, version).toDto()

    override fun updateProjectPartnerStateAid(partnerId: Long, stateAid: ProjectPartnerStateAidDTO): ProjectPartnerStateAidDTO =
        updateProjectPartnerStateAid.updatePartnerStateAid(partnerId, stateAid.toModel()).toDto()

    override fun deleteProjectPartner(partnerId: Long) {
        return deleteProjectPartner.deletePartner(partnerId)
    }
}
