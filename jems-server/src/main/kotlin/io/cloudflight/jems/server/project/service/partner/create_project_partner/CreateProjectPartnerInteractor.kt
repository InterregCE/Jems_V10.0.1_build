package io.cloudflight.jems.server.project.service.partner.create_project_partner

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail

interface CreateProjectPartnerInteractor {

    fun create(projectId: Long, projectPartner: ProjectPartner): ProjectPartnerDetail

}
