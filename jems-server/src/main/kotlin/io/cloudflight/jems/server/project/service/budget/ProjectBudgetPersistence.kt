package io.cloudflight.jems.server.project.service.budget

import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import java.math.BigDecimal

interface ProjectBudgetPersistence {

    fun getStaffCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getTravelCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getExternalCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getEquipmentCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>
    fun getInfrastructureCosts(partnerIds: Set<Long>, projectId: Long, version: String? = null): List<ProjectPartnerCost>

    fun getLumpSumContributionPerPartner(partnerIds: Set<Long>, projectId: Long, version: String? = null): Map<Long, BigDecimal>
    fun getUnitCostsPerPartner(partnerIds: Set<Long>, projectId: Long, version: String? = null): Map<Long, BigDecimal>

    fun getPartnersForProjectId(projectId: Long, version: String? = null): List<ProjectPartner>

}
