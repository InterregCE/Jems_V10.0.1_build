package io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringProjectBudget

import java.math.BigDecimal

interface GetContractingMonitoringProjectBudgetInteractor {

    fun getProjectBudget(projectId: Long, version: String?): BigDecimal
}
