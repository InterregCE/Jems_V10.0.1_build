package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectPartnerReportIdentificationSummary
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfileReportedValues
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.calculateDifferenceFromPlan
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.calculateDifferenceFromPlanPercentage
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetProjectReportIdentification(
    private val projectReportIdentificationPersistence: ProjectReportIdentificationPersistence,
    private val projectReportCertificatePersistence: ProjectReportCertificatePersistence,
    private val projectReportPersistence: ProjectReportPersistence,
) : GetProjectReportIdentificationInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportIdentificationException::class)
    override fun getIdentification(projectId: Long, reportId: Long): ProjectReportIdentification {
        return projectReportIdentificationPersistence.getReportIdentification(projectId, reportId).apply {
            spendingProfiles = getProjectReportSpendingProfiles(projectId, reportId)
        }
    }

    private fun getProjectReportSpendingProfiles(projectId: Long, reportId: Long): List<ProjectReportSpendingProfile> {
        val isClosed = projectReportPersistence.getReportById(projectId, reportId).status.isClosed()
        val reportedValues: Map<Long, ProjectReportSpendingProfileReportedValues> = projectReportIdentificationPersistence
            .getSpendingProfileReportedValues(reportId)
            .associateBy { it.partnerId }

        val certificatesIncluded = projectReportCertificatePersistence
            .getIdentificationSummariesOfProjectReport(projectReportId = reportId)
            .groupBy { it.partnerId }

        val profiles = certificatesIncluded.map { (partnerId, certificates) ->
            val certificatesSorted = certificates.sortedByDescending { it.reportNumber }
            val lastCertified = certificatesSorted.first()
            val currentReport = getCurrentlyReportedValue(isClosed, reportedValues, partnerId, certificates)
            val previouslyReported = reportedValues[partnerId]?.previouslyReported ?: BigDecimal.ZERO
            ProjectReportSpendingProfile(
                partnerRole = lastCertified.partnerRole,
                partnerNumber = lastCertified.partnerNumber,
                periodDetail = lastCertified.periodDetail,
                currentReport = currentReport,
                previouslyReported = previouslyReported,
                nextReportForecast = lastCertified.nextReportForecast,
                differenceFromPlan = calculateDifferenceFromPlan(
                    lastCertified.periodDetail, previouslyReported.plus(currentReport)
                ),
                differenceFromPlanPercentage = calculateDifferenceFromPlanPercentage(
                    lastCertified.periodDetail,
                    previouslyReported.plus(currentReport)
                )
            )
        }.sortedWith(compareByDescending<ProjectReportSpendingProfile> { it.partnerRole }.thenBy { it.partnerNumber })
        return profiles
    }

    private fun getCurrentlyReportedValue(
        isClosed: Boolean,
        reportedValues: Map<Long, ProjectReportSpendingProfileReportedValues>,
        partnerId: Long,
        certificates: List<ProjectPartnerReportIdentificationSummary>
    ): BigDecimal {
        return if (isClosed)
            reportedValues[partnerId]?.currentlyReported ?: BigDecimal.ZERO
        else
            certificates.sumOf { it.sumTotalEligibleAfterControl }
    }

}
