package io.cloudflight.jems.server.project.service.workpackage.investment.update_work_package_investment

import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateWorkPackageInvestment(
    private val workPackagePersistence: WorkPackagePersistence
) : UpdateWorkPackageInvestmentInteractor {

    @PreAuthorize("hasAuthority('ProjectUpdate') || @projectWorkPackageAuthorization.canOwnerUpdateProjectInvestment(#workPackageInvestment.id)")
    @Transactional
    override fun updateWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment) =
        workPackagePersistence.updateWorkPackageInvestment(workPackageId, workPackageInvestment)
}
