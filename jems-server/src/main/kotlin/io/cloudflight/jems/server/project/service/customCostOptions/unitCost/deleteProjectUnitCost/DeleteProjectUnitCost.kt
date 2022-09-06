package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.deleteProjectUnitCost

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectForm
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectUnitCost(
    private val projectUnitCostPersistence: ProjectUnitCostPersistence,
    private val projectPartnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
) : DeleteProjectUnitCostInteractor {

    @CanUpdateProjectForm
    @Transactional
    @ExceptionWrapper(DeleteProjectUnitCostException::class)
    override fun deleteProjectUnitCost(projectId: Long, unitCostId: Long) {
        if (!projectUnitCostPersistence.existProjectUnitCost(projectId, unitCostId = unitCostId)) {
            throw ProjectUnitCostNotFound()
        }
        if (projectPartnerBudgetCostsPersistence.isUnitCostUsed(unitCostId)) {
            throw ProjectUnitCostIsInUse()
        }
        projectUnitCostPersistence.deleteProjectUnitCost(projectId, unitCostId = unitCostId)
    }

}
