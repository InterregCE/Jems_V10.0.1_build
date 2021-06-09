package io.cloudflight.jems.server.project.service.workpackage.investment.add_work_package_investment

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.authorization.CanUpdateProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AddWorkPackageInvestment(
    private val workPackagePersistence: WorkPackagePersistence
) : AddWorkPackageInvestmentInteractor {

    companion object {
        private const val MAX_INVESTMENT_PER_WORK_PACKAGE = 20L
    }

    @CanUpdateProject
    @Transactional
    override fun addWorkPackageInvestment(projectId: Long, workPackageId: Long, workPackageInvestment: WorkPackageInvestment): Long {
        validateInvestmentsMaxCount(workPackageId = workPackageId)
        return workPackagePersistence.addWorkPackageInvestment(workPackageId, workPackageInvestment)
    }

    private fun validateInvestmentsMaxCount(workPackageId: Long) {
        if (workPackagePersistence.countWorkPackageInvestments(workPackageId) >= MAX_INVESTMENT_PER_WORK_PACKAGE)
            throw I18nValidationException(i18nKey = "project.workPackage.investment.max.allowed.reached")
    }
}
