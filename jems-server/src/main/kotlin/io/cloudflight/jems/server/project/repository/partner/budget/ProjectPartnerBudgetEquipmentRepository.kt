package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.general.equipment.ProjectPartnerBudgetEquipmentEntity
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetEquipmentRepository :
    ProjectPartnerBaseBudgetRepository<ProjectPartnerBudgetEquipmentEntity> {

    fun existsByUnitCostId(unitCostId: Long): Boolean

}
