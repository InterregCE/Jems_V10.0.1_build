package io.cloudflight.jems.server.project.repository.report.project.financialOverview.lumpSums

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportCertificateLumpSumPersistenceProvider(
    private val reportLumpSumRepository: ReportProjectCertificateLumpSumRepository,
) : ProjectReportCertificateLumpSumPersistence {

    @Transactional(readOnly = true)
    override fun getLumpSums(projectId: Long, reportId: Long) =
        reportLumpSumRepository
            .findByReportEntityProjectIdAndReportEntityIdOrderByOrderNrAscIdAsc(projectId = projectId, reportId = reportId)
            .map { CertificateLumpSumBreakdownLine(
                reportLumpSumId = it.id,
                lumpSumId = it.programmeLumpSum.id,
                name = it.programmeLumpSum.translatedValues.mapTo(HashSet()) {
                    InputTranslation(it.translationId.language, it.name)
                },
                period = it.periodNumber,
                orderNr = it.orderNr,
                totalEligibleBudget = it.total,
                previouslyReported = it.previouslyReported,
                previouslyPaid = it.previouslyPaid,
                currentReport = it.current,
                previouslyVerified = it.previouslyVerified,
                currentVerified = it.currentVerified,
            ) }


    @Transactional(readOnly = true)
    override fun getReportedLumpSumCumulative(reportIds: Set<Long>) =
        reportLumpSumRepository.findReportedCumulativeForReportIds(reportIds)
            .associate { Pair(it.first, it.second) }

    @Transactional(readOnly = true)
    override fun getVerifiedLumpSumCumulative(reportIds: Set<Long>) =
        reportLumpSumRepository.findVerifiedCumulativeForReportIds(reportIds)
            .associate { Pair(it.first, it.second) }


    @Transactional
    override fun updateCurrentlyReportedValues(projectId: Long, reportId: Long, currentValues: Map<Int, BigDecimal>) {
        reportLumpSumRepository
            .findByReportEntityProjectIdAndReportEntityIdOrderByOrderNrAscIdAsc(projectId = projectId, reportId = reportId)
            .forEach {
                if (currentValues.containsKey(it.orderNr)) {
                    it.current = currentValues.get(it.orderNr)!!
                }
            }
    }
}
