package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment

import io.cloudflight.jems.server.project.authorization.CanReadProjectWorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class GetWorkPackageInvestment(
    private val workPackagePersistence: WorkPackagePersistence
) : GetWorkPackageInvestmentInteractor {

    @CanReadProjectWorkPackageInvestment
    @Transactional(readOnly = true)
    override fun getWorkPackageInvestment(investmentId: UUID) =
        workPackagePersistence.getWorkPackageInvestment(investmentId)

}
