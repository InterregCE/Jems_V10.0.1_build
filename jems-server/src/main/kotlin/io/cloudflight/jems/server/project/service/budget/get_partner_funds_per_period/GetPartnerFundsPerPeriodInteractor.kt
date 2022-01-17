package io.cloudflight.jems.server.project.service.budget.get_partner_funds_per_period

import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectPartnerFundsPerPeriod

interface GetPartnerFundsPerPeriodInteractor {
    fun getPartnerFundsPerPeriod(projectId: Long, version: String? = null): List<ProjectPartnerFundsPerPeriod>
}
