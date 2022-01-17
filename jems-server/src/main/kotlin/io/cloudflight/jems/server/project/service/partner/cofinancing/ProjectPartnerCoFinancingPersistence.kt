package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing

interface ProjectPartnerCoFinancingPersistence {

    fun getAvailableFunds(partnerId: Long): Set<ProgrammeFund>

    fun getCoFinancingAndContributions(
        partnerId: Long,
        version: String? = null
    ): ProjectPartnerCoFinancingAndContribution

    fun getCoFinancingAndContributionsForPartnerList(
        partnerIds: List<Long>,
        projectId: Long,
        version: String? = null
    ): Map<Long, List<ProjectPartnerCoFinancing>>?

    fun updateCoFinancingAndContribution(
        partnerId: Long,
        finances: List<UpdateProjectPartnerCoFinancing>,
        partnerContributions: List<ProjectPartnerContribution>
    ): ProjectPartnerCoFinancingAndContribution

}
