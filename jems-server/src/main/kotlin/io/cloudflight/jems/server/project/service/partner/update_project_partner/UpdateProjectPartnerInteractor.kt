package io.cloudflight.jems.server.project.service.partner.update_project_partner

import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO

interface UpdateProjectPartnerInteractor {

    fun update(projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail

    fun updatePartnerAddresses(partnerId: Long, addresses: Set<ProjectPartnerAddressDTO>): OutputProjectPartnerDetail

    fun updatePartnerContacts(partnerId: Long, contacts: Set<InputProjectContact>): OutputProjectPartnerDetail

    fun updatePartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivationDTO): OutputProjectPartnerDetail

}
