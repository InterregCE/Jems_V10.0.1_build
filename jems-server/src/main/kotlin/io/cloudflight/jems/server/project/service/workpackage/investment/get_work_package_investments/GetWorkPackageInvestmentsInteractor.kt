package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investments

import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetWorkPackageInvestmentsInteractor {
    fun getWorkPackageInvestments(workPackageId: Long, pageable: Pageable): Page<WorkPackageInvestment>
}
