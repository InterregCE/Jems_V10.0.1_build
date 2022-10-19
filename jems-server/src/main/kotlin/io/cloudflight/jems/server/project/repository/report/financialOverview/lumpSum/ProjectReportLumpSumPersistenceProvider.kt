package io.cloudflight.jems.server.project.repository.report.financialOverview.lumpSum

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportLumpSumRepository
import io.cloudflight.jems.server.project.service.report.model.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportLumpSumPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportLumpSumPersistenceProvider(
    private val reportLumpSumRepository: ProjectPartnerReportLumpSumRepository,
) : ProjectReportLumpSumPersistence {

    @Transactional(readOnly = true)
    override fun getLumpSum(partnerId: Long, reportId: Long) =
        reportLumpSumRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(partnerId = partnerId, reportId = reportId)
            .map { ExpenditureLumpSumBreakdownLine(
                reportLumpSumId = it.id,
                lumpSumId = it.programmeLumpSum.id,
                name = it.programmeLumpSum.translatedValues.mapTo(HashSet()) {
                    InputTranslation(it.translationId.language, it.name)
                },
                period = it.period,
                totalEligibleBudget = it.total,
                previouslyReported = it.previouslyReported,
                currentReport = it.current,
            ) }

    @Transactional(readOnly = true)
    override fun getLumpSumCumulative(reportIds: Set<Long>) =
        reportLumpSumRepository.findCumulativeForReportIds(reportIds).toMap()

    @Transactional
    override fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReported: Map<Long, BigDecimal>,
    ) {
        reportLumpSumRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(partnerId = partnerId, reportId = reportId)
            .forEach {
                if (currentlyReported.containsKey(it.id)) {
                    it.current = currentlyReported.get(it.id)!!
                }
            }
    }
}
