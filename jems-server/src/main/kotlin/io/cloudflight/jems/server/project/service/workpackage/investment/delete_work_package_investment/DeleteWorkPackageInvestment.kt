package io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment

import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DeleteWorkPackageInvestment(
    private val workPackagePersistence: WorkPackagePersistence
) : DeleteWorkPackageInvestmentInteractor {

    @CanUpdateProject
    @Transactional
    override fun deleteWorkPackageInvestment(projectId: Long, workPackageInvestmentId: UUID) =
        workPackagePersistence.deleteWorkPackageInvestment(workPackageInvestmentId)
}
