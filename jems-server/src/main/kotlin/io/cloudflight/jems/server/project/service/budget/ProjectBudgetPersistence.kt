package io.cloudflight.jems.server.project.service.budget

import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudget
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import java.math.BigDecimal

interface ProjectBudgetPersistence {

    fun getStaffCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getTravelCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getExternalCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getEquipmentCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getInfrastructureCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>

    fun getLumpSumContributionPerPartner(partnerIds: Set<Long>, projectId: Long, version: String? = null): Map<Long, BigDecimal>
    fun getUnitCostsPerPartner(partnerIds: Set<Long>, projectId: Long, version: String? = null): Map<Long, BigDecimal>
    fun getBudgetPerPartner(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerBudget>

    fun getPartnersForProjectId(projectId: Long, version: String? = null): List<ProjectPartnerSummary>

}
