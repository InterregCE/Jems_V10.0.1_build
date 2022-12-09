package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.unitCost

import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportUnitCostRepository
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
