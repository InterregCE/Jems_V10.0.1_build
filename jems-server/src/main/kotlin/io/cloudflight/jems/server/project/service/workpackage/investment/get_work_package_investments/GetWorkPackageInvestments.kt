package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investments

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetWorkPackageInvestments(
    private val workPackagePersistence: WorkPackagePersistence
) : GetWorkPackageInvestmentsInteractor {

    @CanRetrieveProjectWorkPackage
    @Transactional(readOnly = true)
    override fun getWorkPackageInvestments(workPackageId: Long, version: String?) =
        workPackagePersistence.getWorkPackageInvestments(workPackageId, version)
}
