package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.lumpSum

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportLumpSumRepository
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectPartnerReportLumpSumPersistenceProvider(
    private val reportLumpSumRepository: ProjectPartnerReportLumpSumRepository,
) : ProjectPartnerReportLumpSumPersistence {

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
                fastTrack = it.programmeLumpSum.isFastTrack,
                period = it.period,
                totalEligibleBudget = it.total,
                previouslyReported = it.previouslyReported,
                previouslyPaid = it.previouslyPaid,
                currentReport = it.current,
                previouslyReportedParked = it.previouslyReportedParked,
                currentReportReIncluded = it.currentReIncluded,
                totalEligibleAfterControl = it.totalEligibleAfterControl,
                previouslyValidated = it.previouslyValidated,
            ) }

    @Transactional(readOnly = true)
    override fun getLumpSumCumulative(reportIds: Set<Long>) =
        reportLumpSumRepository.findCumulativeForReportIds(reportIds)
            .associate { Pair(it.first, ExpenditureLumpSumCurrent(current = it.second, currentParked = it.third)) }

    @Transactional
    override fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReported: Map<Long, ExpenditureLumpSumCurrentWithReIncluded>,
    ) {
        reportLumpSumRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(partnerId = partnerId, reportId = reportId)
            .forEach {
                if (currentlyReported.containsKey(it.id)) {
                    it.current = currentlyReported.get(it.id)!!.current
                    it.currentReIncluded = currentlyReported.get(it.id)!!.currentReIncluded
                }
            }
    }

    @Transactional
    override fun updateAfterControlValues(
        partnerId: Long,
        reportId: Long,
        afterControl: Map<Long, ExpenditureLumpSumCurrent>,
    ) {
        reportLumpSumRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(partnerId = partnerId, reportId = reportId)
            .forEach {
                if (afterControl.containsKey(it.id)) {
                    it.totalEligibleAfterControl = afterControl.get(it.id)!!.current
                    it.currentParked = afterControl.get(it.id)!!.currentParked
                }
            }
    }

    @Transactional(readOnly = true)
    override fun getLumpSumCumulativeAfterControl(reportIds: Set<Long>) =
        reportLumpSumRepository.findCumulativeAfterControlForReportIds(reportIds)
            .associate { Pair(it.first, it.second) }
}
