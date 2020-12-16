package io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment

import java.util.*

interface DeleteWorkPackageInvestmentInteractor {
    fun deleteWorkPackageInvestment(investmentId: UUID)
}
