package io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment

import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AddWorkPackageInvestment(
    private val workPackagePersistence: WorkPackagePersistence
) : AddWorkPackageInvestmentInteractor {

    @CanUpdateProject
    @Transactional
    override fun addWorkPackageInvestment(projectId: Long, workPackageId: Long, workPackageInvestment: WorkPackageInvestment) =
        workPackagePersistence.addWorkPackageInvestment(workPackageId, workPackageInvestment)
}
