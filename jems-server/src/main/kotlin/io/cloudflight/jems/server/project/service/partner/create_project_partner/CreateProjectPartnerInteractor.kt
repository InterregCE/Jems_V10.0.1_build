package io.cloudflight.jems.server.project.service.partner.create_project_partner

import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail

interface CreateProjectPartnerInteractor {

    fun create(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail

}