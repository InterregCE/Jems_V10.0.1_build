package io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringProjectBudget

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview.GetProjectCoFinancingOverviewCalculatorService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetContractingMonitoringProjectBudget(
    private val getProjectCoFinancingOverviewCalculatorService: GetProjectCoFinancingOverviewCalculatorService
): GetContractingMonitoringProjectBudgetInteractor {

    @CanRetrieveProjectContractingMonitoring
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetContractingMonitoringProjectBudgetException::class)
    override fun getProjectBudget(projectId: Long, version: String?): BigDecimal {
        val coFinancingOverview = getProjectCoFinancingOverviewCalculatorService.getProjectCoFinancingOverview(projectId, version)
        return coFinancingOverview.projectManagementCoFinancing.totalFundAndContribution.add(
            coFinancingOverview.projectSpfCoFinancing.totalFundAndContribution)
    }
}
