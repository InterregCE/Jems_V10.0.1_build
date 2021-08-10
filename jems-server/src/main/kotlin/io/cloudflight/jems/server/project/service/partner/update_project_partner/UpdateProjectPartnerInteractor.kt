package io.cloudflight.jems.server.project.service.partner.update_project_partner

import io.cloudflight.jems.api.project.dto.ProjectContactDTO
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.partner.UpdateProjectPartnerRequestDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO

interface UpdateProjectPartnerInteractor {

    fun update(projectPartner: UpdateProjectPartnerRequestDTO): ProjectPartnerDetailDTO

    fun updatePartnerAddresses(partnerId: Long, addresses: Set<ProjectPartnerAddressDTO>): ProjectPartnerDetailDTO

    fun updatePartnerContacts(partnerId: Long, contacts: Set<ProjectContactDTO>): ProjectPartnerDetailDTO

    fun updatePartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivationDTO): ProjectPartnerDetailDTO

}
