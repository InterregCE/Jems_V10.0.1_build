package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.unitCost

import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportUnitCostRepository
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectPartnerReportUnitCostPersistenceProvider(
    private val reportUnitCostRepository: ProjectPartnerReportUnitCostRepository,
) : ProjectPartnerReportUnitCostPersistence {

    @Transactional(readOnly = true)
    override fun getUnitCost(partnerId: Long, reportId: Long) =
        reportUnitCostRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(partnerId = partnerId, reportId = reportId)
            .map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getUnitCostCumulative(reportIds: Set<Long>) =
        reportUnitCostRepository.findCumulativeForReportIds(reportIds)
            .associate { Pair(it.first, ExpenditureUnitCostCurrent(current = it.second, currentParked = it.third)) }

    @Transactional(readOnly = true)
    override fun getVerificationParkedUnitCostCumulative(partnerId:Long, projectReportIds: Set<Long>): Map<Long, BigDecimal> =
        reportUnitCostRepository.findVerificationParkedCumulativeForProjectReportIds(partnerId, projectReportIds).toMap()

    @Transactional
    override fun getValidatedUnitCostCumulative(reportIds: Set<Long>): Map<Long, BigDecimal> =
        reportUnitCostRepository.findCumulativeForReportIdsAfterControl(reportIds)
            .associate { Pair(it.first, it.second) }

    @Transactional
    override fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReported: Map<Long, ExpenditureUnitCostCurrentWithReIncluded>,
    ) {
        reportUnitCostRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(partnerId = partnerId, reportId = reportId)
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
        afterControl: Map<Long, ExpenditureUnitCostCurrent>,
    ) {
        reportUnitCostRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(partnerId = partnerId, reportId = reportId)
            .forEach {
                if (afterControl.containsKey(it.id)) {
                    it.totalEligibleAfterControl = afterControl.get(it.id)!!.current
                    it.currentParked = afterControl.get(it.id)!!.currentParked
                }
            }
    }


    @Transactional
    override fun updateAfterVerificationParkedValues(parkedValuesPerCertificate: Map<Long, Map<Long, BigDecimal>>, ) {
        reportUnitCostRepository.findAllByReportEntityIdIn(parkedValuesPerCertificate.keys)
            .groupBy { it.reportEntity.id }
            .forEach {
                val afterVerificationParked = parkedValuesPerCertificate[it.key]!!
                it.value.forEach { unitCostEntity ->
                    if (afterVerificationParked.containsKey(unitCostEntity.programmeUnitCost.id)) {
                        unitCostEntity.currentParkedVerification =
                            afterVerificationParked[unitCostEntity.programmeUnitCost.id]!!
                    }
                }
            }
    }

    @Transactional(readOnly = true)
    override fun getUnitCostCumulativeAfterControl(reportIds: Set<Long>) =
        reportUnitCostRepository.findCumulativeForReportIdsAfterControl(reportIds)
            .associate { Pair(it.first, it.second) }

}
