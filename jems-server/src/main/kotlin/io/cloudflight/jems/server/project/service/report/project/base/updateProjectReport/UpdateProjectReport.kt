package io.cloudflight.jems.server.project.service.report.project.base.updateProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportUpdate
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceModel
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
    private val deadlinePersistence: ContractingReportingPersistence,
    private val certificatePersistence: ProjectReportCertificatePersistence
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

        val existingDeadlineType = getExistingDeadlineType(projectId, reportPersistence.getReportById(projectId, reportId))
        val linkedDeadline = if (data.deadlineId != null) deadlinePersistence.getContractingReportingDeadline(projectId, data.deadlineId) else null
        if (isFinanceTypeExcluded(existingDeadlineType, data, linkedDeadline)) {
            val certificates = certificatePersistence.listCertificatesOfProjectReport(reportId)
            if (certificates.isNotEmpty()) {
                certificates.forEach {
                    certificatePersistence.deselectCertificate(reportId, it.id)
                }
            }
        }

        return reportPersistence.updateReport(
            projectId = projectId,
            reportId = reportId,
            startDate = data.startDate,
            endDate = data.endDate,
            deadline =  data.toDeadlineObject(validDeadlineIdResolver = { getValidDeadlineId(linkedDeadline!!) }),
        ).toServiceModel(periodResolver = { periodNumber -> periods[periodNumber]!! })
    }

    private fun getValidDeadlineId(deadline: ProjectContractingReportingSchedule) = deadline.id

    private fun getExistingDeadlineType(projectId: Long, existingReport: ProjectReportModel): ContractingDeadlineType? {
        return if (existingReport.deadlineId == null)
            existingReport.type
        else
            deadlinePersistence.getContractingReportingDeadline(projectId, existingReport.deadlineId).type
    }

    private fun isFinanceTypeExcluded(
        existingDeadlineType: ContractingDeadlineType?,
        data: ProjectReportUpdate,
        newLinkedDeadline: ProjectContractingReportingSchedule?
    ): Boolean {
        if (existingDeadlineType == ContractingDeadlineType.Content)
            return false
        return if (data.isManual()) {
            data.type == ContractingDeadlineType.Content
        } else {
            newLinkedDeadline?.type == ContractingDeadlineType.Content
        }
    }
}
