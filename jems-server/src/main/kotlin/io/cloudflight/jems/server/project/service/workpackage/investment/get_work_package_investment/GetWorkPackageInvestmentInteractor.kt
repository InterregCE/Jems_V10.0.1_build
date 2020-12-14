package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment

import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import java.util.*

interface GetWorkPackageInvestmentInteractor {
    fun getWorkPackageInvestment(projectId: Long, workPackageInvestmentId: UUID): WorkPackageInvestment
}
