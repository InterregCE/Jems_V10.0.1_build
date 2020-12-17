package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetEquipmentEntity
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetEquipmentCostRepository
    : ProjectPartnerBudgetCommonRepository<ProjectPartnerBudgetEquipmentEntity>
