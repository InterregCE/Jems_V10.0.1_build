package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investments

import io.cloudflight.jems.server.project.authorization.CanReadProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetWorkPackageInvestments(
    private val workPackagePersistence: WorkPackagePersistence
) : GetWorkPackageInvestmentsInteractor {

    @CanReadProjectWorkPackage
    @Transactional(readOnly = true)
    override fun getWorkPackageInvestments(workPackageId: Long, pageable: Pageable) =
        workPackagePersistence.getWorkPackageInvestments(workPackageId, pageable)
}
