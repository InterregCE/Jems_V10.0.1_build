package io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing

import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingPrevious
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
    override fun getCoFinancingCumulative(submittedReportIds: Set<Long>, finalizedReportIds: Set<Long>): ReportCertificateCoFinancingPrevious {
        val cumulativeCurrentForSubmitted = with(certificateCoFinancingRepository.findCumulativeCurrentForReportIds(submittedReportIds)) {
            ReportCertificateCoFinancingColumn(
                funds = projectReportCoFinancingRepository.findCumulativeCurrentForReportIds(submittedReportIds)
                    .associateBy({ it.reportFundId }, { it.sum }),
                partnerContribution = partnerContribution,
                publicContribution = publicContribution,
                automaticPublicContribution = automaticPublicContribution,
                privateContribution = privateContribution,
                sum = sum,
            )
        }
        val cumulativeVerifiedForFinalized = with(certificateCoFinancingRepository.findCumulativeVerifiedForReportIds(finalizedReportIds)) {
            ReportCertificateCoFinancingColumn(
                funds = projectReportCoFinancingRepository.findCumulativeVerifiedForReportIds(finalizedReportIds)
                    .associateBy({ it.reportFundId }, { it.sum }),
                partnerContribution = partnerContribution,
                publicContribution = publicContribution,
                automaticPublicContribution = automaticPublicContribution,
                privateContribution = privateContribution,
                sum = sum,
            )
        }
        return ReportCertificateCoFinancingPrevious(
            previouslyReported = cumulativeCurrentForSubmitted,
            previouslyVerified = cumulativeVerifiedForFinalized,
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
