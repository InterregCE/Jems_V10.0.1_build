package io.cloudflight.jems.server.project.service.partner.create_project_partner

import io.cloudflight.jems.api.project.dto.partner.CreateProjectPartnerRequestDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO

interface CreateProjectPartnerInteractor {

    fun create(projectId: Long, projectPartner: CreateProjectPartnerRequestDTO): ProjectPartnerDetailDTO

}
