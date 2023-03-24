package io.cloudflight.jems.server.project.repository.report.project.financialOverview.investment

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateInvestmentPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportCertificateInvestmentPersistenceProvider(
    private val reportInvestmentRepository: ReportProjectCertificateInvestmentRepository,
) : ProjectReportCertificateInvestmentPersistence {
    @Transactional(readOnly = true)
    override fun getInvestments(projectId: Long, reportId: Long): List<CertificateInvestmentBreakdownLine> =
        reportInvestmentRepository
            .findByReportEntityProjectIdAndReportEntityIdOrderByIdAsc(projectId = projectId, reportId = reportId)
            .map { CertificateInvestmentBreakdownLine(
                reportInvestmentId = it.id,
                investmentId = it.investmentId,
                investmentNumber = it.investmentNumber,
                workPackageNumber = it.workPackageNumber,
                title = it.translatedValues.mapTo(HashSet()) {
                    InputTranslation(it.translationId.language, it.title)
                },
                deactivated = it.deactivated,
                totalEligibleBudget = it.total,
                previouslyReported = it.previouslyReported,
                currentReport = it.current,
            ) }

    @Transactional(readOnly = true)
    override fun getInvestmentCumulative(reportIds: Set<Long>): Map<Long, BigDecimal> =
        reportInvestmentRepository.findCumulativeForReportIds(reportIds)
            .associate { Pair(it.first, it.second) }

    @Transactional
    override fun updateCurrentlyReportedValues(projectId: Long, reportId: Long, currentValues: Map<Long, BigDecimal>) {
        reportInvestmentRepository
            .findByReportEntityProjectIdAndReportEntityIdOrderByIdAsc(projectId = projectId, reportId = reportId)
            .forEach {
                if (currentValues.containsKey(it.investmentId)) {
                    it.current = currentValues.get(it.investmentId)!!
                }
            }
    }
}
