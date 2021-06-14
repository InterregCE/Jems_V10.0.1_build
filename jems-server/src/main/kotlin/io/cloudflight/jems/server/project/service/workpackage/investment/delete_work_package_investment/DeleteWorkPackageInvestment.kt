package io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectWorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteWorkPackageInvestment(
    private val workPackagePersistence: WorkPackagePersistence
) : DeleteWorkPackageInvestmentInteractor {

    @CanUpdateProjectWorkPackageInvestment
    @Transactional
    override fun deleteWorkPackageInvestment(projectId: Long, workPackageId: Long, investmentId: Long) =
        workPackagePersistence.deleteWorkPackageInvestment(workPackageId, investmentId)
}
