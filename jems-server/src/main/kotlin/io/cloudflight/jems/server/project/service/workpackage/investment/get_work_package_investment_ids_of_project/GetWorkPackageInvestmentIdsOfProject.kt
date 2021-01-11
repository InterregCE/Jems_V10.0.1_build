package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment_ids_of_project

import io.cloudflight.jems.server.project.authorization.CanReadProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetWorkPackageInvestmentIdsOfProject(
    private val workPackagePersistence: WorkPackagePersistence
) : GetWorkPackageInvestmentIdsOfProjectInteractor {

    @CanReadProject
    @Transactional(readOnly = true)
    override fun getWorkPackageInvestmentIds(projectId: Long) =
        workPackagePersistence.getWorkPackageInvestmentIdsOfProject(projectId)
}
