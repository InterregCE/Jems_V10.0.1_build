package io.cloudflight.jems.server.project.controller.partner

import io.cloudflight.jems.api.project.partner.ProjectPartnerApi
import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.ProjectPartnerStateAidDTO
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
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
    private val getProjectPartnerInteractor: GetProjectPartnerInteractor,
    private val createProjectPartnerInteractor: CreateProjectPartnerInteractor,
    private val updateProjectPartnerInteractor: UpdateProjectPartnerInteractor,
    private val getProjectPartnerStateAidInteractor: GetProjectPartnerStateAidInteractor,
    private val updateProjectPartnerStateAidInteractor: UpdateProjectPartnerStateAidInteractor,
    private val deleteProjectPartnerInteractor: DeleteProjectPartnerInteractor,
) : ProjectPartnerApi {

    override fun getProjectPartners(projectId: Long, pageable: Pageable, version: String?): Page<OutputProjectPartner> {
        return getProjectPartnerInteractor.findAllByProjectId(projectId, pageable, version)
    }

    override fun getProjectPartnersForDropdown(projectId: Long, pageable: Pageable, version: String?): List<OutputProjectPartner> {
        return getProjectPartnerInteractor.findAllByProjectIdForDropdown(projectId, pageable.sort, version)
    }

    override fun getProjectPartnerById(partnerId: Long, version: String?): OutputProjectPartnerDetail {
        return getProjectPartnerInteractor.getById(partnerId, version)
    }

    override fun createProjectPartner(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail {
        return createProjectPartnerInteractor.create(projectId = projectId, projectPartner = projectPartner)
    }

    override fun updateProjectPartner(projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail {
        return updateProjectPartnerInteractor.update(projectPartner)
    }

    override fun updateProjectPartnerAddress(partnerId: Long, addresses: Set<ProjectPartnerAddressDTO>): OutputProjectPartnerDetail {
        return updateProjectPartnerInteractor.updatePartnerAddresses(partnerId, addresses)
    }

    override fun updateProjectPartnerContact(partnerId: Long, contacts: Set<InputProjectContact>): OutputProjectPartnerDetail {
        return updateProjectPartnerInteractor.updatePartnerContacts(partnerId, contacts)
    }

    override fun updateProjectPartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivationDTO): OutputProjectPartnerDetail {
        return updateProjectPartnerInteractor.updatePartnerMotivation(partnerId, motivation)
    }

    override fun getProjectPartnerStateAid(partnerId: Long, version: String?): ProjectPartnerStateAidDTO =
        getProjectPartnerStateAidInteractor.getStateAidForPartnerId(partnerId, version).toDto()

    override fun updateProjectPartnerStateAid(partnerId: Long, stateAid: ProjectPartnerStateAidDTO): ProjectPartnerStateAidDTO =
        updateProjectPartnerStateAidInteractor.updatePartnerStateAid(partnerId, stateAid.toModel()).toDto()

    override fun deleteProjectPartner(projectId: Long, partnerId: Long) {
        return deleteProjectPartnerInteractor.deletePartner(partnerId)
    }
}
