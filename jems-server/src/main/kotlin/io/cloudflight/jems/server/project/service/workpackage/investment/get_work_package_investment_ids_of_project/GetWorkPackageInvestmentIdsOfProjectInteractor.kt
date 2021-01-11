package io.cloudflight.jems.server.project.service.workpackage.investment.get_work_package_investment_ids_of_project

interface GetWorkPackageInvestmentIdsOfProjectInteractor {
    fun getWorkPackageInvestmentIds(projectId: Long): List<Long>
}
