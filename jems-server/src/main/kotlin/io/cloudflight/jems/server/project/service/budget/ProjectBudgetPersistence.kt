package io.cloudflight.jems.server.project.service.budget

import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerCost
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner

interface ProjectBudgetPersistence {

    fun getStaffCosts(partnerIds: Set<Long>): List<ProjectPartnerCost>
    fun getTravelCosts(partnerIds: Set<Long>): List<ProjectPartnerCost>
    fun getExternalCosts(partnerIds: Set<Long>): List<ProjectPartnerCost>
    fun getEquipmentCosts(partnerIds: Set<Long>): List<ProjectPartnerCost>
    fun getInfrastructureCosts(partnerIds: Set<Long>): List<ProjectPartnerCost>

    fun getPartnersForProjectId(projectId: Long): List<ProjectPartner>

}
