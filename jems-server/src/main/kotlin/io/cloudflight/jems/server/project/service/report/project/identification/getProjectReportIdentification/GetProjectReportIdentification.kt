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
import io.cloudflight.jems.server.project.service.report.model.project.identification.SpendingProfileTotal
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.calculateDifferenceFromPlan
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.calculateDifferenceFromPlanPercentage
import io.cloudflight.jems.server.project.service.report.percentageOf
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class GetProjectReportIdentification(
    private val projectReportIdentificationPersistence: ProjectReportIdentificationPersistence,
    private val projectReportCertificatePersistence: ProjectReportCertificatePersistence,
    private val projectReportPersistence: ProjectReportPersistence,
    private val partnerPersistence: PartnerPersistence,
) : GetProjectReportIdentificationInteractor {


    companion object {
        fun emptySpendingProfile() = ProjectReportSpendingProfile(
            lines = emptyList(),
            total = SpendingProfileTotal(
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
            val totalReportedSoFar = previouslyReported.plus(currentReport)

            SpendingProfileLine(
                partnerRole = partnerDetails.role,
                partnerNumber = partnerDetails.sortNumber!!,
                partnerAbbreviation = partnerDetails.nameInEnglish ?: "",
                partnerCountry = partnerCountry,
                currentReport = currentReport,
                previouslyReported = previouslyReported,
                totalEligibleBudget = totalEligibleBudget,
                totalReportedSoFar =  totalReportedSoFar,
                totalReportedSoFarPercentage = totalReportedSoFar.multiply(BigDecimal.valueOf(100))
                    .divide(totalEligibleBudget, 2, RoundingMode.HALF_UP),
                remainingBudget =  totalEligibleBudget.minus(totalReportedSoFar),

                periodBudget = lastCertified?.periodDetail?.periodBudget ?: BigDecimal.ZERO,
                periodBudgetCumulative = lastCertified?.periodDetail?.periodBudgetCumulative ?: BigDecimal.ZERO,
                nextReportForecast = lastCertified?.nextReportForecast ?: BigDecimal.ZERO,
                differenceFromPlan = calculateDifferenceFromPlan(lastCertified?.periodDetail, totalReportedSoFar),
                differenceFromPlanPercentage = calculateDifferenceFromPlanPercentage(lastCertified?.periodDetail, totalReportedSoFar)
            )
        }.sortedWith(compareByDescending<SpendingProfileLine> { it.partnerRole }.thenBy { it.partnerNumber })




      val totalSpendingProfile =  profiles.fold(emptySpendingProfile().total) { total, spendingProfile  ->
          total.periodBudget += spendingProfile.periodBudget
          total.periodBudgetCumulative += spendingProfile.periodBudgetCumulative
          total.differenceFromPlan += spendingProfile.differenceFromPlan
          total.nextReportForecast += spendingProfile.nextReportForecast
          total.totalEligibleBudget += spendingProfile.totalEligibleBudget
          total.currentReport += spendingProfile.currentReport
          total.previouslyReported += spendingProfile.previouslyReported
          total.totalReportedSoFar += spendingProfile.totalReportedSoFar
          total.remainingBudget += spendingProfile.remainingBudget
          return@fold total
      }.calculatePercentages()

        return ProjectReportSpendingProfile(
            lines = profiles,
            total = totalSpendingProfile
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

    fun SpendingProfileTotal.calculatePercentages(): SpendingProfileTotal {
        totalReportedSoFarPercentage = totalReportedSoFar.percentageOf(totalEligibleBudget) ?: BigDecimal.ZERO
        differenceFromPlanPercentage = totalReportedSoFar.percentageOf(periodBudgetCumulative) ?: BigDecimal.ZERO
        return this
    }

}
