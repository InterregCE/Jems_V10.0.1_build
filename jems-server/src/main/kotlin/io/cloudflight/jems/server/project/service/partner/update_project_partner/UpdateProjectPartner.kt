package io.cloudflight.jems.server.project.service.partner.update_project_partner

import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class UpdateProjectPartner(
    private val persistence: PartnerPersistence,
) : UpdateProjectPartnerInteractor {

    @PreAuthorize("hasAuthority('ProjectUpdate') || @projectPartnerAuthorization.canOwnerUpdatePartner(#projectPartner.id)")
    override fun update(projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail =
        persistence.update(projectPartner)

    @CanUpdateProjectPartner
    override fun updatePartnerAddresses(partnerId: Long, addresses: Set<ProjectPartnerAddressDTO>): OutputProjectPartnerDetail =
        persistence.updatePartnerAddresses(partnerId, addresses)

    @CanUpdateProjectPartner
    override fun updatePartnerContacts(partnerId: Long, contacts: Set<InputProjectContact>): OutputProjectPartnerDetail =
        persistence.updatePartnerContacts(partnerId, contacts)

    @CanUpdateProjectPartner
    override fun updatePartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivationDTO): OutputProjectPartnerDetail =
        persistence.updatePartnerMotivation(partnerId, motivation)
}