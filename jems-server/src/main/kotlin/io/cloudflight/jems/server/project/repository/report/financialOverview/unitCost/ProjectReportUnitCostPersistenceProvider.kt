package io.cloudflight.jems.server.project.repository.report.financialOverview.unitCost

import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportUnitCostRepository
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportUnitCostPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportUnitCostPersistenceProvider(
    private val reportUnitCostRepository: ProjectPartnerReportUnitCostRepository,
) : ProjectReportUnitCostPersistence {

    @Transactional(readOnly = true)
    override fun getUnitCost(partnerId: Long, reportId: Long) =
        reportUnitCostRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(partnerId = partnerId, reportId = reportId)
            .map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getUnitCostCumulative(reportIds: Set<Long>) =
        reportUnitCostRepository.findCumulativeForReportIds(reportIds).toMap()

    @Transactional
    override fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReported: Map<Long, BigDecimal>,
    ) {
        reportUnitCostRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(partnerId = partnerId, reportId = reportId)
            .forEach {
                if (currentlyReported.containsKey(it.id)) {
                    it.current = currentlyReported.get(it.id)!!
                }
            }
    }

}
