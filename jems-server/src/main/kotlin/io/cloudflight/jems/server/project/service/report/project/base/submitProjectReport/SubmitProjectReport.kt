package io.cloudflight.jems.server.project.service.report.project.base.submitProjectReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryCurrentlyReported
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import io.cloudflight.jems.server.project.service.report.project.projectReportSubmitted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class SubmitProjectReport(
    private val reportPersistence: ProjectReportPersistence,
    private val reportCertificatePersistence: ProjectReportCertificatePersistence,
    private val reportIdentificationPersistence: ProjectReportIdentificationPersistence,
    private val reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence,
    private val reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : SubmitProjectReportInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(SubmitProjectReportException::class)
    override fun submit(projectId: Long, reportId: Long): ProjectReportStatus {
        val report = reportPersistence.getReportById(projectId, reportId)
        val certificates = reportCertificatePersistence.listCertificatesOfProjectReport(reportId)
        validateReportIsStillDraft(report)
        updateSpendingProfileReportedValues(reportId)
        saveCurrentCoFinancing(certificates, projectId, reportId)
        saveCurrentCostCategories(certificates, projectId, reportId)

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
        certificates: List<ProjectPartnerReportSubmissionSummary>,
        projectId: Long,
        reportId: Long,
    ) {
        val certificateCurrentValues = reportExpenditureCoFinancingPersistence.getCoFinancingTotalEligible(certificates.map { it.id}.toSet())

        reportCertificateCoFinancingPersistence.updateCurrentlyReportedValues(
            projectId = projectId,
            reportId = reportId,
            currentlyReported = certificateCurrentValues
        )
    }

    private fun saveCurrentCostCategories(
        certificates: List<ProjectPartnerReportSubmissionSummary>,
        projectId: Long,
        reportId: Long
    ) {
        val currentValues =
            reportExpenditureCostCategoryPersistence.getCostCategoriesCumulativeTotalEligible(certificates.map {it.id}.toSet())

        reportCertificateCostCategoryPersistence.updateCurrentlyReportedValues(
            projectId = projectId,
            reportId = reportId,
            currentlyReported = CertificateCostCategoryCurrentlyReported(
                currentlyReported = currentValues
            ),
        )
    }

}
