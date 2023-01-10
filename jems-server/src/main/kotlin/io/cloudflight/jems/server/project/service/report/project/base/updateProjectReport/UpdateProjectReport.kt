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

        private fun ProjectReportUpdate.deadlineManual() = ProjectReportDeadline(
            deadlineId = null,
            type = type,
            periodNumber = periodNumber,
            reportingDate = reportingDate,
        )

        fun ProjectReportUpdate.validateInput(
            validPeriodNumbers: Set<Int>,
            datesInvalidExceptionResolver: () -> Exception,
            linkToDeadlineWithManualDataExceptionResolver: () -> Exception,
            noLinkAndDataMissingExceptionResolver: () -> Exception,
            periodNumberExceptionResolver: (Int) -> Exception,
        ) {
            validateDates(this, datesInvalidExceptionResolver)
            when {
                this.isLink() -> validateLink(this, linkToDeadlineWithManualDataExceptionResolver)
                this.isManual() -> validateManual(data = this, validPeriodNumbers = validPeriodNumbers,
                    noLinkAndDataMissingExceptionResolver = noLinkAndDataMissingExceptionResolver,
                    periodNumberExceptionResolver = periodNumberExceptionResolver)
            }
        }

        private fun validateDates(data: ProjectReportUpdate, datesInvalidExceptionResolver: () -> Exception) {
            if (data.startDate != null && data.endDate != null && data.startDate.isAfter(data.endDate))
                throw datesInvalidExceptionResolver.invoke()
        }

        private fun validateLink(data: ProjectReportUpdate, linkToDeadlineWithManualDataExceptionResolver: () -> Exception) {
            if (data.containsManualValues())
                throw linkToDeadlineWithManualDataExceptionResolver.invoke()
        }

        private fun validateManual(
            data: ProjectReportUpdate,
            validPeriodNumbers: Set<Int>,
            noLinkAndDataMissingExceptionResolver: () -> Exception,
            periodNumberExceptionResolver: (Int) -> Exception,
        ) {
            if (data.isMissingManualValues())
                throw noLinkAndDataMissingExceptionResolver.invoke()

            if (data.periodNumber!! !in validPeriodNumbers)
                throw periodNumberExceptionResolver.invoke(data.periodNumber)
        }

        private fun ProjectReportUpdate.containsManualValues() =
            type != null || periodNumber != null || reportingDate != null

        private fun ProjectReportUpdate.isMissingManualValues() =
            type == null || periodNumber == null || reportingDate == null

        private fun ProjectReportUpdate.isLink() = deadlineId != null

        private fun ProjectReportUpdate.isManual() = !isLink()

        private fun ProjectReportUpdate.toDeadlineObject(validDeadlineIdResolver: (Long) -> Long) =
            if (isManual())
                deadlineManual()
            else
                ProjectReportDeadline(
                    deadlineId = validDeadlineIdResolver.invoke(deadlineId!!),
                    type = null,
                    periodNumber = null,
                    reportingDate = null,
                )

    }

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateProjectReportException::class)
    override fun updateReport(projectId: Long, reportId: Long, data: ProjectReportUpdate): ProjectReport {
        val version = reportPersistence.getReportById(projectId, reportId).linkedFormVersion
        val periods = projectPersistence.getProjectPeriods(projectId, version).associateBy { it.number }

        data.validateInput(validPeriodNumbers = periods.keys,
            datesInvalidExceptionResolver = { StartDateIsAfterEndDate() },
            linkToDeadlineWithManualDataExceptionResolver = { LinkToDeadlineProvidedWithManualDataOverride() },
            noLinkAndDataMissingExceptionResolver = { LinkToDeadlineNotProvidedAndDataMissing() },
            periodNumberExceptionResolver = { PeriodNumberInvalid(it) },
        )
        val deadline = data.toDeadlineObject(validDeadlineIdResolver = { getValidDeadlineId(projectId, it) })

        return reportPersistence.updateReport(
            projectId = projectId,
            reportId = reportId,
            startDate = data.startDate,
            endDate = data.endDate,
            deadline = deadline,
        ).toServiceModel(periodResolver = { periodNumber -> periods[periodNumber]!! })
    }

    private fun getValidDeadlineId(projectId: Long, deadlineId: Long) =
        deadlinePersistence.getContractingReportingDeadline(projectId, deadlineId).id

}
