package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment

import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment

interface GetWorkPackageInvestmentInteractor {
    fun getWorkPackageInvestment(investmentId: Long, version: String? = null): WorkPackageInvestment
}
