package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetProjectPartnerReportIdentification(
    private val identificationPersistence: ProjectReportIdentificationPersistence,
    private val reportPersistence: ProjectReportPersistence,
) : GetProjectPartnerReportIdentificationInteractor {

    companion object {
        private fun emptyIdentification() = ProjectPartnerReportIdentification(
            startDate = null,
            endDate = null,
            summary = emptySet(),
            problemsAndDeviations = emptySet(),
            spendingDeviations = emptySet(),
            targetGroups = emptyList(),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = null,
                currentReport = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                differenceFromPlan = BigDecimal.ZERO,
                differenceFromPlanPercentage = BigDecimal.ZERO,
                nextReportForecast = BigDecimal.ZERO,
            )
        )
    }

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportIdentificationException::class)
    override fun getIdentification(partnerId: Long, reportId: Long): ProjectPartnerReportIdentification {
        val identification = identificationPersistence
            .getPartnerReportIdentification(partnerId = partnerId, reportId = reportId)
            .orElse(emptyIdentification())

        val isNotSubmitted = !reportPersistence.getPartnerReportById(partnerId = partnerId, reportId).status.isClosed()

        return identification.fillInSpendingProfile(
            isOpen = isNotSubmitted,
            currentReportResolver = { BigDecimal.ONE } /* TODO calculate and fill in */
        )
    }
}
