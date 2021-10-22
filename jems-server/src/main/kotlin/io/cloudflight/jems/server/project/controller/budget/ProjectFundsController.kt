package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.project.budget.ProjectFundsApi
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerFundsPerPeriodDTO
import io.cloudflight.jems.server.project.service.budget.get_partner_funds_per_period.GetPartnerFundsPerPeriodInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectFundsController(
    private val getPartnerFundsPerPeriodInteractor: GetPartnerFundsPerPeriodInteractor,
) : ProjectFundsApi {

    override fun getProjectPartnerFundsPerPeriod(projectId: Long, version: String?): List<ProjectPartnerFundsPerPeriodDTO> =
        this.getPartnerFundsPerPeriodInteractor.getPartnerFundsPerPeriod(projectId, version).map { it.toDto() }
}
