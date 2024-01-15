package io.cloudflight.jems.server.project.repository.report.project.verification.financialOverview

import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.PartnerCertificateFundSplit
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.ProjectReportFinancialOverviewPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportFinancialOverviewPersistenceProvider(
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val projectReportCoFinancingRepository: ProjectReportCoFinancingRepository,
    private val projectReportCoFinancingOverviewRepository: ProjectReportVerificationCertificateCoFinancingOverviewRepository,
    private val projectReportCoFinancingSpfOverviewRepository: ProjectReportVerificationCertificateSpfCoFinancingOverviewRepository,
    private val projectReportRepository: ProjectReportRepository,
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
                spfLine = false,
                partnerId = lineTotal.partnerReport.partnerId,
                partnerRole = lineTotal.partnerReport.identification.partnerRole,
                partnerNumber = lineTotal.partnerReport.identification.partnerNumber,
                fundsSorted = splits.map { Pair(it.programmeFund!!.toModel(), it.fundValue!!) },
                partnerContribution = lineTotal.partnerContribution,
                publicContribution = lineTotal.publicContribution,
                automaticPublicContribution = lineTotal.automaticPublicContribution,
                privateContribution = lineTotal.privateContribution,
                total = lineTotal.total,
                split = splits.toSplitLineModelList(),
            )

        }
        return coFinancingOverviewLines
    }

    @Transactional(readOnly = true)
    override fun getOverviewSpfPerFund(projectReportId: Long): FinancingSourceBreakdownLine? {
        val certificateFinancingSources =
            projectReportCoFinancingSpfOverviewRepository.findAllByProjectReportId(projectReportId)

        if (certificateFinancingSources.isEmpty())
            return null

        val lineTotal = certificateFinancingSources.first { it.programmeFund == null }
        val splits = certificateFinancingSources.filter { it.programmeFund != null }

        return FinancingSourceBreakdownLine(
            partnerReportId = null,
            partnerReportNumber = null,
            spfLine = true,
            partnerId = null,
            partnerRole = null,
            partnerNumber = null,
            fundsSorted = splits.map { Pair(it.programmeFund!!.toModel(), it.fundValue!!) },
            partnerContribution = lineTotal.partnerContribution,
            publicContribution = lineTotal.publicContribution,
            automaticPublicContribution = lineTotal.automaticPublicContribution,
            privateContribution = lineTotal.privateContribution,
            total = lineTotal.total,
            split = splits.toSpfSplitLineModelList(),
        )
    }

    @Transactional
    override fun storeOverviewPerFund(
        projectReportId: Long,
        toStore: List<FinancingSourceBreakdownLine>,
        spfPartnerIdInCaseOfSpf: Long?,
    ): List<PartnerCertificateFundSplit>  {
        val certificates = partnerReportRepository.findAllByProjectReportId(projectReportId).associateBy { it.id }
        val availableFunds =
            projectReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(projectReportId)
                .mapNotNull { it.programmeFund }.associateBy { it.id }

        val lines = toStore.filter { !it.spfLine }.flatMap { certificate ->
            val partnerReport = certificates[certificate.partnerReportId]!!
            val certificateLine = certificate.toEntity(partnerReport)
            val certificateSplits = certificate.split.toEntities(partnerReport, fundsResolver = { availableFunds[it]!! })
            return@flatMap listOf(certificateLine).plus(certificateSplits)
        }
        val projectReport = projectReportRepository.getById(projectReportId)
        val linesSpf = toStore.filter { it.spfLine }.flatMap { certificate ->
            val certificateLine = certificate.toSpfEntity(projectReport)
            val certificateSplits = certificate.split.toSpfEntities(projectReport, fundsResolver = { availableFunds[it]!! })
            return@flatMap listOf(certificateLine).plus(certificateSplits)
        }

        projectReportCoFinancingOverviewRepository.deleteAllByPartnerReportProjectReportId(projectReportId)
        projectReportCoFinancingOverviewRepository.flush()
        val regular = projectReportCoFinancingOverviewRepository.saveAll(lines)
            .filter { it.programmeFund != null }.toModel()

        projectReportCoFinancingSpfOverviewRepository.deleteAllByProjectReportId(projectReportId)
        projectReportCoFinancingSpfOverviewRepository.flush()
        val spf = projectReportCoFinancingSpfOverviewRepository.saveAll(linesSpf)
            .filter { it.programmeFund != null }.toModel(partnerId = spfPartnerIdInCaseOfSpf)

        return regular.plus(spf)
    }
}
