package io.cloudflight.jems.server.project.service.lumpsum.get_project_lump_sums

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectPartner
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectLumpSums(
    private val persistence: ProjectLumpSumPersistence,
    private val budgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
) : GetProjectLumpSumsInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    @ExceptionWrapper(GetProjectLumpSumsException::class)
    override fun getLumpSums(projectId: Long, version: String?) = persistence.getLumpSums(projectId, version)

    @Transactional(readOnly = true)
    @CanRetrieveProjectPartner
    @ExceptionWrapper(GetProjectLumpSumsPerPartnerException::class)
    override fun getLumpSumsTotalForPartner(partnerId: Long, version: String?) =
        budgetCostsPersistence.getBudgetLumpSumsCostTotal(partnerId, version)

}
