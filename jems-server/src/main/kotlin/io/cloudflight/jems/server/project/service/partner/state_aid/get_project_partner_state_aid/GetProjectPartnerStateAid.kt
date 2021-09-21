package io.cloudflight.jems.server.project.service.partner.state_aid.get_project_partner_state_aid

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartner
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerStateAid(
    private val persistence: PartnerPersistence,
) : GetProjectPartnerStateAidInteractor {

    @CanRetrieveProjectPartner
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerStateAidException::class)
    override fun getStateAidForPartnerId(partnerId: Long, version: String?): ProjectPartnerStateAid =
        persistence.getPartnerStateAid(partnerId, version)

}
