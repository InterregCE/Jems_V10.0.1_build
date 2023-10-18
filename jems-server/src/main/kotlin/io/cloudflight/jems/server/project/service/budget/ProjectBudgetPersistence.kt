package io.cloudflight.jems.server.project.service.budget

import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudget
import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.budget.model.ProjectSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.PartnerTotalBudgetPerCostCategory
import io.cloudflight.jems.server.project.service.unitcost.model.ProjectUnitCost
import java.math.BigDecimal

interface ProjectBudgetPersistence {

    fun getStaffCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getTravelCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getExternalCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getEquipmentCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getInfrastructureCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getSpfCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>

    fun getLumpSumContributionPerPartner(partnerIds: Set<Long>, projectId: Long, version: String? = null): Map<Long, BigDecimal>
    fun getUnitCostsPerPartner(partnerIds: Set<Long>, projectId: Long, version: String? = null): Map<Long, BigDecimal>
    fun getBudgetPerPartner(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerBudget>

    fun getPartnersForProjectId(projectId: Long, version: String? = null): List<ProjectPartnerSummary>

    fun getBudgetTotalForPartners(partnerIds: Set<Long>, projectId: Long, version: String? = null): Map<Long, PartnerTotalBudgetPerCostCategory>

    fun getProjectUnitCosts(projectId: Long, version: String? = null): List<ProjectUnitCost>

    fun getSpfBudgetPerPeriod(partnerId: Long, projectId:Long, version: String?): List<ProjectSpfBudgetPerPeriod>
}
