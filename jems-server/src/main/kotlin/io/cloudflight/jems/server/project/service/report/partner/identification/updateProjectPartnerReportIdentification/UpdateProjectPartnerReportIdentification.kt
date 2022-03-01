package io.cloudflight.jems.server.project.service.report.partner.identification.updateProjectPartnerReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.UpdateProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectPartnerReportIdentification(
    private val reportPersistence: ProjectReportPersistence,
    private val reportIdentificationPersistence: ProjectReportIdentificationPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val projectPersistence: ProjectPersistence,
    private val generalValidator: GeneralValidatorService,
) : UpdateProjectPartnerReportIdentificationInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerReportIdentificationException::class)
    override fun updateIdentification(
        partnerId: Long,
        reportId: Long,
        data: UpdateProjectPartnerReportIdentification
    ): ProjectPartnerReportIdentification {
        val reportMetadata = reportPersistence.getPartnerReportStatusAndVersion(partnerId, reportId = reportId)

        validateReportNotClosed(status = reportMetadata.status)
        validateInputs(data = data)
        validatePeriod(
            period = data.period,
            validPeriods = projectPersistence.getProjectPeriods(
                projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, version = reportMetadata.version),
                version = reportMetadata.version,
            )
        )

        return reportIdentificationPersistence.updatePartnerReportIdentification(
            partnerId = partnerId,
            reportId = reportId,
            data = data,
        )
    }

    private fun validateReportNotClosed(status: ReportStatus) {
        if (status.isClosed())
            throw ReportAlreadyClosed()
    }

    private fun validateInputs(data: UpdateProjectPartnerReportIdentification) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(data.summary, 2000, "summary"),
            generalValidator.maxLength(data.problemsAndDeviations, 2000, "problemsAndDeviations"),
            *data.targetGroups.mapIndexed { index, it ->
                generalValidator.maxLength(it, 2000, "descriptionOfTheTargetGroup[$index]")
            }.toTypedArray(),
            generalValidator.startDateBeforeEndDate(data.startDate, data.endDate, "startDate", "endDate"),
        )
    }

    private fun validatePeriod(period: Int?, validPeriods: List<ProjectPeriod>) {
        if (period != null && !validPeriods.mapTo(HashSet()) { it.number }.contains(period))
            throw InvalidPeriodNumber(periodNumber = period)
    }

}
