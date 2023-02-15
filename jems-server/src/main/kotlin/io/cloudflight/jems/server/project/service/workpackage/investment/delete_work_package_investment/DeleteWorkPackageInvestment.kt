package io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment

import io.cloudflight.jems.server.project.authorization.CanDeleteProjectWorkPackageInvestment
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteWorkPackageInvestment(
    private val projectPersistence: ProjectPersistence,
    private val workPackagePersistence: WorkPackagePersistence
) : DeleteWorkPackageInvestmentInteractor {

    @CanDeleteProjectWorkPackageInvestment
    @Transactional
    override fun deleteWorkPackageInvestment(projectId: Long, workPackageId: Long, investmentId: Long) {
        val status = projectPersistence.getApplicantAndStatusById(projectId).projectStatus
        if (status.isAlreadyContracted())
            workPackagePersistence.deactivateWorkPackageInvestment(workPackageId, investmentId)
        else
            workPackagePersistence.deleteWorkPackageInvestment(workPackageId, investmentId)
    }
}
