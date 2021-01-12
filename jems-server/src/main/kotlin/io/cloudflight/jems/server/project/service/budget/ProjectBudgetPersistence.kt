package io.cloudflight.jems.server.project.service.budget

import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import java.math.BigDecimal
import java.util.UUID

interface ProjectBudgetPersistence {

    fun getStaffCosts(partnerIds: Set<Long>): List<ProjectPartnerCost>
    fun getTravelCosts(partnerIds: Set<Long>): List<ProjectPartnerCost>
    fun getExternalCosts(partnerIds: Set<Long>): List<ProjectPartnerCost>
    fun getEquipmentCosts(partnerIds: Set<Long>): List<ProjectPartnerCost>
    fun getInfrastructureCosts(partnerIds: Set<Long>): List<ProjectPartnerCost>

    fun getLumpSumContributionPerPartner(lumpSumIds: Set<UUID>): Map<Long, BigDecimal>

    fun getPartnersForProjectId(projectId: Long): List<ProjectPartner>

}
