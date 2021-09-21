package io.cloudflight.jems.server.project.service.partner.state_aid.update_project_partner_state_aid

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid

interface UpdateProjectPartnerStateAidInteractor {

    fun updatePartnerStateAid(partnerId: Long, stateAid: ProjectPartnerStateAid): ProjectPartnerStateAid

}
