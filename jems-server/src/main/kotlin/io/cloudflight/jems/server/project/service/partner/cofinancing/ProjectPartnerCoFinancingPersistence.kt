package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing

interface ProjectPartnerCoFinancingPersistence {

    fun getAvailableFundIds(partnerId: Long): Set<Long>

    fun getCoFinancingAndContributions(
        partnerId: Long,
        version: String? = null
    ): ProjectPartnerCoFinancingAndContribution

    fun updateCoFinancingAndContribution(
        partnerId: Long,
        finances: Collection<UpdateProjectPartnerCoFinancing>,
        partnerContributions: List<ProjectPartnerContribution>
    ): ProjectPartnerCoFinancingAndContribution

}
