package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment

import io.cloudflight.jems.server.project.authorization.CanRetrieveProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetWorkPackageInvestment(
    private val workPackagePersistence: WorkPackagePersistence
) : GetWorkPackageInvestmentInteractor {

    @CanRetrieveProject
    @Transactional(readOnly = true)
    override fun getWorkPackageInvestment(projectId: Long, investmentId: Long, version: String?) =
        workPackagePersistence.getWorkPackageInvestment(investmentId, projectId, version)
}
