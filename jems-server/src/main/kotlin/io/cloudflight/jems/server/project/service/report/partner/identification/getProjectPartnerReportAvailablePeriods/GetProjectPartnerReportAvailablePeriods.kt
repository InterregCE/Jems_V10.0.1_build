package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportAvailablePeriods

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportIdentificationPersistence
import io.cloudflight.jems.server.common.toLimits
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GetProjectPartnerReportAvailablePeriods(
    private val reportIdentificationPersistence: ProjectPartnerReportIdentificationPersistence,
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val partnerPersistence: PartnerPersistence,
) : GetProjectPartnerReportAvailablePeriodsInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportAvailablePeriodsException::class)
    override fun get(partnerId: Long, reportId: Long): List<ProjectPartnerReportPeriod> {
        val periods = reportIdentificationPersistence.getAvailablePeriods(partnerId = partnerId, reportId = reportId)
            .filterOutPreparationAndClosure()
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        val startDate = contractingMonitoringPersistence.getContractingMonitoring(projectId).startDate

        return periods.calculateDates(startDate = startDate)
    }

    private fun List<ProjectPartnerReportPeriod>.calculateDates(startDate: LocalDate?): List<ProjectPartnerReportPeriod> =
        this.onEach { period ->
            if(startDate != null) {
                val limits = period.toLimits(startDate)
                period.startDate = limits.first
                period.endDate = limits.second
            }
        }
}
