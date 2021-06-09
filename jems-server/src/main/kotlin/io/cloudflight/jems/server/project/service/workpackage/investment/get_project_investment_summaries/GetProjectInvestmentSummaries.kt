package io.cloudflight.jems.server.project.service.workpackage.investment.get_project_investment_summaries

import io.cloudflight.jems.server.project.authorization.CanRetrieveProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectInvestmentSummaries(
    private val workPackagePersistence: WorkPackagePersistence
) : GetProjectInvestmentSummariesInteractor {

    @CanRetrieveProject
    @Transactional(readOnly = true)
    override fun getProjectInvestmentSummaries(projectId: Long, version: String?) =
        workPackagePersistence.getProjectInvestmentSummaries(projectId, version)
}
