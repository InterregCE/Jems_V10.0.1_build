package io.cloudflight.jems.server.project.service.partner.delete_project_partner

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectPartner
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service

@Service
class DeleteProjectPartner(
    private val persistence: PartnerPersistence,
) : DeleteProjectPartnerInteractor {

    @CanUpdateProjectPartner
    override fun deletePartner(partnerId: Long) =
        persistence.deletePartner(partnerId)
}