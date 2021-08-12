package io.cloudflight.jems.server.project.service.partner.cofinancing.update_cofinancing

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing

interface UpdateCoFinancingInteractor {
    fun updateCoFinancing(
        partnerId: Long,
        finances: List<UpdateProjectPartnerCoFinancing>,
        partnerContributions: List<ProjectPartnerContribution>
    ): ProjectPartnerCoFinancingAndContribution
}
