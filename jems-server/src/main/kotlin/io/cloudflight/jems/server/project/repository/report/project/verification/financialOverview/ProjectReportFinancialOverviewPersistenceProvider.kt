package io.cloudflight.jems.server.project.repository.report.project.verification.financialOverview

import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.ProjectReportFinancialOverviewPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportFinancialOverviewPersistenceProvider(
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val projectReportCoFinancingRepository: ProjectReportCoFinancingRepository,
    private val projectReportCoFinancingOverviewRepository: ProjectReportVerificationCertificateCoFinancingOverviewRepository,
) : ProjectReportFinancialOverviewPersistence {

    @Transactional(readOnly = true)
    override fun getOverviewPerFund(projectReportId: Long): List<FinancingSourceBreakdownLine> {
        val certificateFinancingSources =
            projectReportCoFinancingOverviewRepository.findAllByPartnerReportProjectReportId(projectReportId)
                .groupBy { it.partnerReport.id }.mapValues {
                    Pair(
                        it.value.first { it.programmeFund == null },
                        it.value.filter { it.programmeFund != null },
                    )
                }


        val coFinancingOverviewLines = certificateFinancingSources.map {  (certificateId, lineAndSplits) ->
            val lineTotal = lineAndSplits.first
            val splits = lineAndSplits.second

            FinancingSourceBreakdownLine(
                partnerReportId = certificateId,
                partnerReportNumber = lineTotal.partnerReport.number,
                partnerId = lineTotal.partnerReport.partnerId,
                partnerRole = lineTotal.partnerReport.identification.partnerRole,
                partnerNumber = lineTotal.partnerReport.identification.partnerNumber,
                fundsSorted = splits.map { Pair(it.programmeFund!!.toModel(), it.fundValue!!) },
                partnerContribution = lineTotal.partnerContribution,
                publicContribution = lineTotal.publicContribution,
                automaticPublicContribution = lineTotal.automaticPublicContribution,
                privateContribution = lineTotal.privateContribution,
                total = lineTotal.total,
                split = splits.toSplitLineModelList()
            )

        }
        return coFinancingOverviewLines
    }

    @Transactional
    override fun storeOverviewPerFund(
        projectReportId: Long,
        toStore: List<FinancingSourceBreakdownLine>
    ): List<FinancingSourceBreakdownLine> {
        val certificates = partnerReportRepository.findAllByProjectReportId(projectReportId).associateBy { it.id }
        val availableFunds =
            projectReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(projectReportId)
                .mapNotNull { it.programmeFund }.associateBy { it.id }

        val financingSourcesOverview = buildList {
            toStore.map { financingSource ->
                val partnerReport = certificates[financingSource.partnerReportId]!!

                val lineTotal = financingSource.toOverviewEntity(partnerReport = partnerReport, fund = null, fundValue = null)
                add(lineTotal)

                val splits = if ( financingSource.split.isEmpty()) {
                    val fundAndValue = financingSource.fundsSorted.first()
                    listOf(
                        financingSource.toOverviewEntity(
                            partnerReport,
                            fund = availableFunds[fundAndValue.first.id],
                            fundValue = fundAndValue.second
                        )
                    )
                } else { financingSource.split.toOverviewEntityList(partnerReport, availableFunds) }
                addAll(splits)
            }
        }

        projectReportCoFinancingOverviewRepository.saveAll(financingSourcesOverview)
        return getOverviewPerFund(projectReportId)
    }
}
