package io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment

import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment

interface AddWorkPackageInvestmentInteractor {
    fun addWorkPackageInvestment(
        projectId: Long,
        workPackageId: Long,
        workPackageInvestment: WorkPackageInvestment,
    ): Long
}
