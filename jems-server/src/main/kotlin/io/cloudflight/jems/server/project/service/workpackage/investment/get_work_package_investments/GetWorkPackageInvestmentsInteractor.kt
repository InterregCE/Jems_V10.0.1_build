package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investments

import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment

interface GetWorkPackageInvestmentsInteractor {
    fun getWorkPackageInvestments(workPackageId: Long, projectId: Long, version: String? = null): List<WorkPackageInvestment>
}
