package io.cloudflight.jems.server.project.service.workpackage.investment.delete_work_package_investment

interface DeleteWorkPackageInvestmentInteractor {
    fun deleteWorkPackageInvestment(projectId: Long, workPackageId: Long, investmentId: Long)
}
