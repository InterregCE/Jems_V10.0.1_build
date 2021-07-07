package io.cloudflight.jems.server.project.service.partner.state_aid.get_project_partner_state_aid

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid

interface GetProjectPartnerStateAidInteractor {

    fun getStateAidForPartnerId(partnerId: Long, version: String? = null): ProjectPartnerStateAid

}
