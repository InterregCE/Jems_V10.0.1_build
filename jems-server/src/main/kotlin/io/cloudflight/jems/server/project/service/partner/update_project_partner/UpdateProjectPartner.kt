package io.cloudflight.jems.server.project.service.partner.update_project_partner

import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartnerBase
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectPartner(
    private val persistence: PartnerPersistence,
) : UpdateProjectPartnerInteractor {

    @CanUpdateProjectPartnerBase
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerException::class)
    override fun update(projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail =
        persistence.update(projectPartner)

    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerAddressesException::class)
    override fun updatePartnerAddresses(partnerId: Long, addresses: Set<ProjectPartnerAddressDTO>): OutputProjectPartnerDetail =
        persistence.updatePartnerAddresses(partnerId, addresses)

    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerContactsException::class)
    override fun updatePartnerContacts(partnerId: Long, contacts: Set<InputProjectContact>): OutputProjectPartnerDetail =
        persistence.updatePartnerContacts(partnerId, contacts)

    @CanUpdateProjectPartner
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerMotivationException::class)
    override fun updatePartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivationDTO): OutputProjectPartnerDetail =
        persistence.updatePartnerMotivation(partnerId, motivation)
}