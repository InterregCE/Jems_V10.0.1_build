package io.cloudflight.jems.server.project.repository.report.project.financialOverview.unitCosts

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportCertificateUnitCostPersistenceProvider(
    private val reportUnitCostRepository: ReportProjectCertificateUnitCostRepository,
) : ProjectReportCertificateUnitCostPersistence {

    @Transactional(readOnly = true)
    override fun getUnitCosts(projectId: Long, reportId: Long): List<CertificateUnitCostBreakdownLine> =
        reportUnitCostRepository
            .findByReportEntityProjectIdAndReportEntityIdOrderByIdAsc(projectId = projectId, reportId = reportId)
            .map { CertificateUnitCostBreakdownLine(
                reportUnitCostId = it.id,
                unitCostId = it.programmeUnitCost.id,
                name = it.programmeUnitCost.translatedValues.mapTo(HashSet()) {
                    InputTranslation(it.translationId.language, it.name)
                },
                totalEligibleBudget = it.total,
                previouslyReported = it.previouslyReported,
                currentReport = it.current,
                currentVerified = it.currentVerified,
                previouslyVerified = it.previouslyVerified,
            ) }

    @Transactional(readOnly = true)
    override fun getReportedUnitCostsCumulative(reportIds: Set<Long>): Map<Long, BigDecimal> =
        reportUnitCostRepository.findReportedCumulativeForReportIds(reportIds)
            .associate { Pair(it.first, it.second) }

    @Transactional(readOnly = true)
    override fun getVerifiedUnitCostsCumulative(reportIds: Set<Long>): Map<Long, BigDecimal> =
        reportUnitCostRepository.findVerifiedCumulativeForReportIds(reportIds)
            .associate { Pair(it.first, it.second) }

    @Transactional
    override fun updateCurrentlyReportedValues(projectId: Long, reportId: Long, currentValues: Map<Long, BigDecimal>) {
        reportUnitCostRepository
            .findByReportEntityProjectIdAndReportEntityIdOrderByIdAsc(projectId = projectId, reportId = reportId)
            .forEach {
                if (currentValues.containsKey(it.programmeUnitCost.id)) {
                    it.current = currentValues.get(it.programmeUnitCost.id)!!
                }
            }
    }
}
