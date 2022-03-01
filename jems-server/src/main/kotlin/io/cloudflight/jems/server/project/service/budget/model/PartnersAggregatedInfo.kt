package io.cloudflight.jems.server.project.service.budget.model

import io.cloudflight.jems.server.project.service.partner.model.PartnerTotalBudgetPerCostCategory
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary

data class PartnersAggregatedInfo(
    val partners: List<ProjectPartnerSummary>,
    val budgetOptions: List<ProjectPartnerBudgetOptions>,
    val budgetPerPartner: List<ProjectPartnerBudget>,
    val partnersTotalBudgetPerCostCategory: Map<Long, PartnerTotalBudgetPerCostCategory>
){
    fun getBudgetOptionsByPartnerId(partnerId: Long)=
        budgetOptions.firstOrNull { it.partnerId == partnerId }
}
