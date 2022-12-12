package io.cloudflight.jems.server.project.service.report.project.base.updateProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
    private val deadlinePersistence: ContractingReportingPersistence,
) : UpdateProjectReportInteractor {

    companion object {
        private fun deadlineLink(deadlineId: Long) = ProjectReportDeadline(
            deadlineId = deadlineId,
            type = null,
            periodNumber = null,
            reportingDate = null,
        )

        private fun ProjectReportUpdate.deadlineManual() = ProjectReportDeadline(
            deadlineId = null,
            type = type,
            periodNumber = periodNumber,
            reportingDate = reportingDate,
        )
    }

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateProjectReportException::class)
    override fun updateReport(projectId: Long, reportId: Long, data: ProjectReportUpdate): ProjectReport {
        validateDates(data)
        val version = reportPersistence.getReportById(projectId, reportId).linkedFormVersion
        val periods = projectPersistence.getProjectPeriods(projectId, version).associateBy { it.number }

        when {
            data.isLink() -> validateLink(data)
            data.isManual() -> validateManual(data = data, validPeriodNumbers = periods.keys)
        }

        val deadline = if (data.isManual())
            data.deadlineManual()
        else
            deadlineLink(deadlineId = getValidDeadlineId(projectId, data.scheduleId!!))

        return reportPersistence.updateReport(
            projectId = projectId,
            reportId = reportId,
            startDate = data.startDate,
            endDate = data.endDate,
            deadline = deadline,
        ).toServiceModel(periodResolver = { periodNumber -> periods[periodNumber]!! })
    }

    private fun validateDates(data: ProjectReportUpdate) {
        if (data.startDate != null && data.endDate != null && data.startDate.isAfter(data.endDate))
            throw StartDateIsAfterEndDate()
    }

    private fun validateLink(data: ProjectReportUpdate) {
        if (data.containsManualValues())
            throw LinkToDeadlineProvidedWithManualDataOverride()
    }

    private fun validateManual(data: ProjectReportUpdate, validPeriodNumbers: Set<Int>) {
        if (data.isMissingManualValues())
            throw LinkToDeadlineNotProvidedAndDataMissing()

        if (data.periodNumber!! !in validPeriodNumbers)
            throw PeriodNumberInvalid(data.periodNumber)
    }

    private fun getValidDeadlineId(projectId: Long, deadlineId: Long) =
        deadlinePersistence.getContractingReportingDeadline(projectId, deadlineId).id

    private fun ProjectReportUpdate.containsManualValues() =
        type != null || periodNumber != null || reportingDate != null

    private fun ProjectReportUpdate.isMissingManualValues() =
        type == null || periodNumber == null || reportingDate == null

    private fun ProjectReportUpdate.isLink() = scheduleId != null

    private fun ProjectReportUpdate.isManual() = !isLink()

}
