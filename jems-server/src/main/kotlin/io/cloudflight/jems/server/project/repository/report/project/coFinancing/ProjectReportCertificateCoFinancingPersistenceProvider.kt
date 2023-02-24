package io.cloudflight.jems.server.project.repository.report.project.coFinancing

import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportCertificateCoFinancingPersistenceProvider(
    private val certificateCoFinancingRepository: ReportProjectCertificateCoFinancingRepository,
    private val projectReportCoFinancingRepository: ProjectReportCoFinancingRepository,
) : ProjectReportCertificateCoFinancingPersistence {

    @Transactional(readOnly = true)
    override fun getCoFinancing(projectId: Long, reportId: Long): ReportCertificateCoFinancing =
        certificateCoFinancingRepository
            .findFirstByReportEntityProjectIdAndReportEntityId(projectId = projectId, reportId = reportId)
            .toModel(
                coFinancing = projectReportCoFinancingRepository
                    .findAllByIdReportIdOrderByIdFundSortNumber(reportId = reportId),
            )

    @Transactional(readOnly = true)
    override fun getCoFinancingCumulative(reportIds: Set<Long>) =
        with(certificateCoFinancingRepository.findCumulativeForReportIds(reportIds)) {
            ReportCertificateCoFinancingColumn(
                funds = projectReportCoFinancingRepository.findCumulativeForReportIds(reportIds).associateBy({ it.reportFundId }, { it.sum }),
                partnerContribution = partnerContribution,
                publicContribution = publicContribution,
                automaticPublicContribution = automaticPublicContribution,
                privateContribution = privateContribution,
                sum = sum,
            )
        }

    @Transactional
    override fun updateCurrentlyReportedValues(
        projectId: Long,
        reportId: Long,
        currentlyReported: ReportCertificateCoFinancingColumn,
    ) {
        projectReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId)
            .forEachIndexed { index, coFin ->
                coFin.current = currentlyReported.funds.getOrDefault(coFin.programmeFund?.id, BigDecimal.ZERO)
            }

        certificateCoFinancingRepository
            .findFirstByReportEntityProjectIdAndReportEntityId(projectId = projectId, reportId = reportId).apply {
                partnerContributionCurrent = currentlyReported.partnerContribution
                publicContributionCurrent = currentlyReported.publicContribution
                automaticPublicContributionCurrent = currentlyReported.automaticPublicContribution
                privateContributionCurrent = currentlyReported.privateContribution
                sumCurrent = currentlyReported.sum
            }
    }

}
