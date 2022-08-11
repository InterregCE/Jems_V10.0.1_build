package io.cloudflight.jems.server.project.service.contracting.monitoring.getLastApprovedPeriods

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectVersion
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.model.ProjectPeriodForMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.contracting.toLimits
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GetLastApprovedPeriods(
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val versionPersistence: ProjectVersionPersistence,
): GetLastApprovedPeriodsInteractor {

    @CanRetrieveProjectVersion
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetLatestApprovedException::class)
    override fun getPeriods(projectId: Long): List<ProjectPeriodForMonitoring> {
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
        val periods = projectPersistence.getProjectPeriods(projectId, version)
        val startDate = contractingMonitoringPersistence.getContractingMonitoring(projectId).startDate

        return periods.calculateDates(startDate = startDate)
    }

    private fun List<ProjectPeriod>.calculateDates(startDate: LocalDate?): List<ProjectPeriodForMonitoring> {
        if (startDate == null)
            return this.map {
                ProjectPeriodForMonitoring(
                    number = it.number,
                    start = it.start,
                    end = it.end,
                    startDate = null,
                    endDate = null,
                )
            }
        else
            return this.map { it to it.toLimits(startDate) }.toMap().map {
                ProjectPeriodForMonitoring(
                    number = it.key.number,
                    start = it.key.start,
                    end = it.key.end,
                    startDate = it.value.first,
                    endDate = it.value.second,
                )
            }.sortedBy { it.number }
    }

}
