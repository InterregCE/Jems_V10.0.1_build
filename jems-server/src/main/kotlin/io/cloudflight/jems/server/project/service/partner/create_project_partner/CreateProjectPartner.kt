package io.cloudflight.jems.server.project.service.partner.create_project_partner

import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service

@Service
class CreateProjectPartner(
    private val persistence: PartnerPersistence,
) : CreateProjectPartnerInteractor {

    @CanUpdateProject
    override fun create(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail =
        persistence.create(projectId, projectPartner)
}