package io.cloudflight.jems.server.project.service.workpackage.investment.update_work_package_investment

import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateWorkPackageInvestment(
    private val workPackagePersistence: WorkPackagePersistence
) : UpdateWorkPackageInvestmentInteractor {

    @CanUpdateProject
    @Transactional
    override fun updateWorkPackageInvestment(projectId: Long, workPackageInvestment: WorkPackageInvestment) =
        workPackagePersistence.updateWorkPackageInvestment(workPackageInvestment)
}
