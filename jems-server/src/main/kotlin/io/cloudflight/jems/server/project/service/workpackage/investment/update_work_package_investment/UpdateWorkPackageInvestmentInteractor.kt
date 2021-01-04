package io.cloudflight.jems.server.project.service.workpackage.investment.update_work_package_investment

import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment

interface UpdateWorkPackageInvestmentInteractor {
    fun updateWorkPackageInvestment(
        workPackageId: Long,
        workPackageInvestment: WorkPackageInvestment,
    )
}
