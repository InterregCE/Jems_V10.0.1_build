package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerBudgetPerFund(
    private val getPartnerBudgetPerFundService: GetPartnerBudgetPerFundService
) : GetPartnerBudgetPerFundInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    @ExceptionWrapper(GetPartnerBudgetPerFundExceptions::class)
    override fun getProjectPartnerBudgetPerFund(projectId: Long, version: String?): List<ProjectPartnerBudgetPerFund> {
        return getPartnerBudgetPerFundService.getProjectPartnerBudgetPerFund(projectId, version)
    }
}
