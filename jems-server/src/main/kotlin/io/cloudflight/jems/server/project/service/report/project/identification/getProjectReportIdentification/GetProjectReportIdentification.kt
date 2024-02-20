package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectPartnerReportIdentificationSummary
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfileReportedValues
import io.cloudflight.jems.server.project.service.report.model.project.identification.SpendingProfileLine
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.calculateDifferenceFromPlan
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.calculateDifferenceFromPlanPercentage
import io.cloudflight.jems.server.project.service.report.percentageOf
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
    private val partnerPersistence: PartnerPersistence,
) : GetProjectReportIdentificationInteractor {


    companion object {
        fun emptySpendingProfile() = SpendingProfileLine(
            null, null, null, null,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        )
    }

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportIdentificationException::class)
    override fun getIdentification(projectId: Long, reportId: Long): ProjectReportIdentification {
        return projectReportIdentificationPersistence.getReportIdentification(projectId, reportId).apply {
            spendingProfilePerPartner = getProjectReportSpendingProfiles(projectId, reportId)
        }
    }

    fun getProjectReportSpendingProfiles(projectId: Long, reportId: Long): ProjectReportSpendingProfile {
        val projectReport = projectReportPersistence.getReportById(projectId, reportId)
        val projectPartners = partnerPersistence.findTop50ByProjectId(projectId, projectReport.linkedFormVersion)
        val reportedValues: Map<Long, ProjectReportSpendingProfileReportedValues> = projectReportIdentificationPersistence
            .getSpendingProfileReportedValues(reportId)
            .associateBy { it.partnerId }

        val certificatesIncluded = projectReportCertificatePersistence
            .getIdentificationSummariesOfProjectReport(projectReportId = reportId)
            .groupBy { it.partnerId }


        val profiles = projectPartners.map { partnerDetails ->
            val partnerId = partnerDetails.id
            val partnerCountry = partnerDetails.addresses.firstOrNull { it.type ==  ProjectPartnerAddressType.Organization}?.country ?: "N/A"
            val partnerCertificates = certificatesIncluded[partnerId]?.sortedByDescending { it.reportNumber }

            val lastCertified = partnerCertificates?.first()
            val currentReport = getCurrentlyReportedValue(projectReport.status.isClosed(), reportedValues, partnerId, partnerCertificates)
            val previouslyReported = reportedValues[partnerId]?.previouslyReported ?: BigDecimal.ZERO

            val totalEligibleBudget = reportedValues[partnerId]?.partnerTotalEligibleBudget ?: BigDecimal.ZERO

            SpendingProfileLine(
                partnerRole = partnerDetails.role,
                partnerNumber = partnerDetails.sortNumber!!,
                partnerAbbreviation = partnerDetails.nameInEnglish ?: "",
                partnerCountry = partnerCountry,
                currentReport = currentReport,
                previouslyReported = previouslyReported,
                totalEligibleBudget = totalEligibleBudget,

                totalReportedSoFar = BigDecimal.ZERO, // temporary
                totalReportedSoFarPercentage = BigDecimal.ZERO, // temporary
                remainingBudget = BigDecimal.ZERO, // temporary

                periodBudget = lastCertified?.periodDetail?.periodBudget ?: BigDecimal.ZERO,
                periodBudgetCumulative = lastCertified?.periodDetail?.periodBudgetCumulative ?: BigDecimal.ZERO,
                nextReportForecast = lastCertified?.nextReportForecast ?: BigDecimal.ZERO,

                differenceFromPlan = BigDecimal.ZERO, // temporary
                differenceFromPlanPercentage = BigDecimal.ZERO, // temporary
            )
        }.sortedWith(compareByDescending<SpendingProfileLine> { it.partnerRole }.thenBy { it.partnerNumber })

        val totalSpendingProfile = profiles.sumUp()

        return ProjectReportSpendingProfile(
            lines = profiles.fillInOverviewFields(),
            total = totalSpendingProfile.fillInOverviewFields(),
        )
    }

    private fun getCurrentlyReportedValue(
        isClosed: Boolean,
        reportedValues: Map<Long, ProjectReportSpendingProfileReportedValues>,
        partnerId: Long,
        certificates: List<ProjectPartnerReportIdentificationSummary>?
    ): BigDecimal {
        return if (isClosed)
            reportedValues[partnerId]?.currentlyReported ?: BigDecimal.ZERO
        else
            certificates?.sumOf { it.sumTotalEligibleAfterControl } ?: BigDecimal.ZERO
    }

    private fun SpendingProfileLine.fillInOverviewFields() = also {
        it.totalReportedSoFar = previouslyReported.plus(currentReport)
        it.totalReportedSoFarPercentage = totalReportedSoFar.percentageOf(totalEligibleBudget)
        it.remainingBudget = totalEligibleBudget.minus(totalReportedSoFar)

        it.differenceFromPlan = calculateDifferenceFromPlan(periodBudgetCumulative, totalReportedSoFar)
        it.differenceFromPlanPercentage = calculateDifferenceFromPlanPercentage(periodBudgetCumulative, totalReportedSoFar)
    }

    private fun List<SpendingProfileLine>.fillInOverviewFields() = onEach { it.fillInOverviewFields() }

    private fun List<SpendingProfileLine>.sumUp() = fold(emptySpendingProfile()) { total, spendingProfile ->
        total.periodBudget += spendingProfile.periodBudget
        total.periodBudgetCumulative += spendingProfile.periodBudgetCumulative
        total.nextReportForecast += spendingProfile.nextReportForecast
        total.totalEligibleBudget += spendingProfile.totalEligibleBudget
        total.currentReport += spendingProfile.currentReport
        total.previouslyReported += spendingProfile.previouslyReported
        return@fold total
    }

}
