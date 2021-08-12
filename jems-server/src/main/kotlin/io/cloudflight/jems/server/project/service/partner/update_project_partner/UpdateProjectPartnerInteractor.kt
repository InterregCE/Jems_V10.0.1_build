package io.cloudflight.jems.server.project.service.partner.update_project_partner

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerContact
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation

interface UpdateProjectPartnerInteractor {

    fun update(projectPartner: ProjectPartner): ProjectPartnerDetail

    fun updatePartnerAddresses(partnerId: Long, addresses: Set<ProjectPartnerAddress>): ProjectPartnerDetail

    fun updatePartnerContacts(partnerId: Long, contacts: Set<ProjectPartnerContact>): ProjectPartnerDetail

    fun updatePartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivation): ProjectPartnerDetail

}
