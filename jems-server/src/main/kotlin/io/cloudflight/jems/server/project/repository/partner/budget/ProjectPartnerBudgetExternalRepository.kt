package io.cloudflight.jems.server.project.repository.partner.budget

import io.cloudflight.jems.server.project.entity.partner.budget.general.external.ProjectPartnerBudgetExternalEntity
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerBudgetExternalRepository :
    ProjectPartnerBaseBudgetRepository<ProjectPartnerBudgetExternalEntity> {

    fun existsByUnitCostId(unitCostId: Long): Boolean

}
