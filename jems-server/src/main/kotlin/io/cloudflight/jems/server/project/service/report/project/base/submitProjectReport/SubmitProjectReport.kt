package io.cloudflight.jems.server.project.service.report.project.base.submitProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview.sum
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown.toLinesModel
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportSubmitted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.ZonedDateTime

@Service
class SubmitProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val reportCertificatePersistence: ProjectReportCertificatePersistence,
    private val reportIdentificationPersistence: ProjectReportIdentificationPersistence,
    private val reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : SubmitProjectReportInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(SubmitProjectReportException::class)
    override fun submit(projectId: Long, reportId: Long): ProjectReportStatus {
        val report = reportPersistence.getReportById(projectId, reportId)
        validateReportIsStillDraft(report)
        updateSpendingProfileReportedValues(reportId)
        saveCurrentCoFinancing(projectId, reportId)

        return reportPersistence.submitReport(
            projectId = projectId,
            reportId = reportId,
            submissionTime = ZonedDateTime.now()
        ).also {
            auditPublisher.publishEvent(
                projectReportSubmitted(
                    context = this,
                    projectId = projectId,
                    report = it,
                    certificates = reportCertificatePersistence.listCertificatesOfProjectReport(reportId),
                )
            )
        }.status
    }

    private fun updateSpendingProfileReportedValues(reportId: Long) {
        val currentSpendingProfile = reportIdentificationPersistence.getSpendingProfileCurrentValues(reportId)
        reportIdentificationPersistence.updateSpendingProfile(reportId, currentValuesByPartnerId = currentSpendingProfile)
    }

    private fun validateReportIsStillDraft(report: ProjectReportModel) {
        if (report.status.isClosed())
            throw ProjectReportAlreadyClosed()
    }

    private fun saveCurrentCoFinancing(
        projectId: Long,
        reportId: Long,
    ) {
        val data = reportCertificateCoFinancingPersistence.getCoFinancing(projectId = projectId, reportId = reportId)
        val coFinancing = data.toLinesModel()

        val certificates = reportCertificatePersistence.listCertificatesOfProjectReport(reportId).map {
            reportExpenditureCoFinancingPersistence.getCoFinancing(it.partnerId, it.id).totalEligibleAfterControl
        }
        val fundIds = coFinancing.funds.map { it.fundId }

        val certificateCurrentValues = ReportCertificateCoFinancingColumn(
            funds = fundIds.associateBy({it}, {certificates.map { certificate ->  certificate.funds.getOrDefault(it, BigDecimal.ZERO) }.sum()}),
            partnerContribution = certificates.sumOf { it.partnerContribution },
            publicContribution = certificates.sumOf { it.publicContribution },
            automaticPublicContribution = certificates.sumOf { it.automaticPublicContribution },
            privateContribution = certificates.sumOf { it.privateContribution },
            sum = certificates.sumOf { it.sum }
        )

        reportCertificateCoFinancingPersistence.updateCurrentlyReportedValues(
            projectId = projectId,
            reportId = reportId,
            currentlyReported = certificateCurrentValues
        )
    }

}
