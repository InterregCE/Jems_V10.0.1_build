package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing.ProjectReportCertificateCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.PartnerReportInvestmentSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedFund
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportBudget
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportInvestment
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportStatusAndType
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.PaymentCumulativeData
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingPrevious
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryPrevious
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

@Service
class CreateProjectReportBudget(
    private val lumpSumPersistence: ProjectLumpSumPersistence,
    private val getProjectBudget: GetProjectBudget,
    private val reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistenceProvider,
    private val paymentPersistence: PaymentPersistence,
    private val reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence,
    private val getPartnerBudgetPerFundService: GetPartnerBudgetPerFundService,
    private val reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence,
    private val partnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val projectBudgetPersistence: ProjectBudgetPersistence,
    private val reportCertificateUnitCostPersistence: ProjectReportCertificateUnitCostPersistence,
    private val reportInvestmentPersistence: ProjectReportCertificateInvestmentPersistence,
) {

    @Transactional
    fun retrieveBudgetDataFor(
        projectId: Long,
        version: String?,
        investments: List<PartnerReportInvestmentSummary>,
        submittedReports: Collection<ProjectReportStatusAndType>,
    ): ProjectReportBudget {
        val submittedReportIds = submittedReports.mapTo(HashSet()) { it.id }
        val finalizedReportIds = submittedReports.filter { it.status.isFinalized() }.mapTo(HashSet()) { it.id }

        val partnerIds = projectBudgetPersistence.getPartnersForProjectId(projectId = projectId, version).map {it.id!!}.toSet()

        val staffCosts = partnerBudgetCostsPersistence.getBudgetStaffCosts(partnerIds, version)
        val travelCosts = partnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partnerIds, version)
        val externalAndEquipmentAndInfrastructure = partnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partnerIds, version)
            .plus(partnerBudgetCostsPersistence.getBudgetEquipmentCosts(partnerIds, version))
            .plus(partnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partnerIds, version))
        val unitCosts = partnerBudgetCostsPersistence.getBudgetUnitCosts(partnerIds, version)

        val lumpSums = lumpSumPersistence.getLumpSums(projectId, version = version)
        val sumOfPaymentReady = lumpSums.sumOfPaymentReady()

        val previouslyReportedCostCategories = reportCertificateCostCategoryPersistence
            .getCostCategoriesCumulative(submittedReportIds = submittedReportIds, finalizedReportIds = finalizedReportIds)
            .addExtraPaymentReadyFastTrackLumpSums(sumOfPaymentReady)

        val totalFromAF = getPartnerBudgetPerFundService.getProjectPartnerBudgetPerFund(projectId, version)
            .first { it.partner === null }
        val costCategoryBreakdownFromAF = getCostCategoryBreakdownFromAF(projectId, version)

        return ProjectReportBudget(
            coFinancing = toCreateModel(
                totalFromAF = totalFromAF,
                previousValues = reportCertificateCoFinancingPersistence.getCoFinancingCumulative(submittedReportIds, finalizedReportIds),
                totalFastTrackReady = sumOfPaymentReady,
                paymentCumulativeData = paymentPersistence.getFtlsCumulativeForProject(projectId),
            ),
            costCategorySetup = costCategorySetup(
                budget = costCategoryBreakdownFromAF,
                previouslyReported = previouslyReportedCostCategories.previouslyReported,
                previouslyVerified = previouslyReportedCostCategories.previouslyVerified

            ),
            availableLumpSums = lumpSums.toProjectReportLumpSums(
                previouslyReported = reportCertificateLumpSumPersistence.getReportedLumpSumCumulative(submittedReportIds),
                previouslyVerified = reportCertificateLumpSumPersistence.getVerifiedLumpSumCumulative(finalizedReportIds),
                previouslyPaid = paymentPersistence.getPaymentsByProjectId(projectId).byLumpSum()
            ),
            unitCosts = getSetOfUnitCostsWithTotalAndNumberOfUnits(
                staffCosts
                    .plus(travelCosts)
                    .plus(externalAndEquipmentAndInfrastructure)
                    .plus(unitCosts),
                previouslyReported = reportCertificateUnitCostPersistence.getReportedUnitCostsCumulative(submittedReportIds),
                previouslyVerified = reportCertificateUnitCostPersistence.getVerifiedUnitCostsCumulative(finalizedReportIds)
            ),
            investments = investments.toPartnerReportInvestments(
                budgetEntries = externalAndEquipmentAndInfrastructure,
                previouslyReported = reportInvestmentPersistence.getReportedInvestmentCumulative(submittedReportIds),
                previouslyVerified = reportInvestmentPersistence.getVerifiedInvestmentCumulative(finalizedReportIds)
            ),
        )
    }

    private fun costCategorySetup(
        budget: BudgetCostsCalculationResultFull,
        previouslyReported:  BudgetCostsCalculationResultFull,
        previouslyVerified:  BudgetCostsCalculationResultFull,
    ) = ReportCertificateCostCategory(
        totalsFromAF = budget,
        currentlyReported = fillZeros(),
        previouslyReported = previouslyReported,
        previouslyVerified = previouslyVerified,
        currentVerified = BudgetCostsCalculationResultFull(
            staff = ZERO,
            office = ZERO,
            travel = ZERO,
            external = ZERO,
            equipment = ZERO,
            infrastructure = ZERO,
            other = ZERO,
            lumpSum = ZERO,
            unitCost = ZERO,
            spfCost = ZERO,
            sum = ZERO,
        ),

    )

    private fun fillZeros() = BudgetCostsCalculationResultFull(
        staff = ZERO,
        office = ZERO,
        travel = ZERO,
        external = ZERO,
        equipment = ZERO,
        infrastructure = ZERO,
        other = ZERO,
        lumpSum = ZERO,
        unitCost = ZERO,
        spfCost = ZERO,
        sum = ZERO,
    )

    companion object {

        private fun ProjectLumpSum.isReady() = readyForPayment

        private fun toCreateModel(
            totalFromAF: ProjectPartnerBudgetPerFund,
            previousValues: ReportCertificateCoFinancingPrevious,
            totalFastTrackReady: BigDecimal,
            paymentCumulativeData: PaymentCumulativeData,
        ): PreviouslyProjectReportedCoFinancing {
            val fundsFromAF = totalFromAF.budgetPerFund.associateBy({ it.fund!!.id }, { it.value })

            // if some fund got removed in AF during modification, add all other already-used funds
            val allFundIds = previousValues.getAllFundIds()
            val missingFundIds = allFundIds.minus(fundsFromAF.keys)
            val funds = fundsFromAF.plus(missingFundIds.map { Pair(it, ZERO) })
                .plus(Pair(null, totalFromAF.totalPartnerContribution))
                .map { (fundId, amount) ->
                    PreviouslyProjectReportedFund(
                        fundId = fundId,
                        total = amount,
                        previouslyReported = previousValues.previouslyReported.funds.getOrDefault(fundId, ZERO),
                        previouslyVerified = previousValues.previouslyVerified.funds.getOrDefault(fundId, ZERO),
                        previouslyPaid = if (fundId == null) ZERO else
                            paymentCumulativeData.confirmedAndPaid.getOrDefault(fundId, ZERO),
                    )
                }

            // calculate values to be added from payments
            val sumOfAllPaymentAmountsToFunds = paymentCumulativeData.amounts.funds.values.sumOf { it }
            val partnerContributionPaymentLeftover = totalFastTrackReady.minus(sumOfAllPaymentAmountsToFunds)

            val cumulativePaymentValues = ReportCertificateCoFinancingColumn(
                // partner contribution calculated as total minus all fund payment amounts
                funds = paymentCumulativeData.amounts.funds
                    .plus(Pair(null, partnerContributionPaymentLeftover)),
                partnerContribution = partnerContributionPaymentLeftover,
                publicContribution = paymentCumulativeData.amounts.publicContribution,
                automaticPublicContribution = paymentCumulativeData.amounts.automaticPublicContribution,
                privateContribution = paymentCumulativeData.amounts.privateContribution,
                sum = totalFastTrackReady,
            )

            return PreviouslyProjectReportedCoFinancing(
                fundsSorted = funds,

                totalPartner = totalFromAF.totalPartnerContribution,
                totalPublic = totalFromAF.publicContribution,
                totalAutoPublic = totalFromAF.autoPublicContribution,
                totalPrivate = totalFromAF.privateContribution,
                totalSum = totalFromAF.totalEligibleBudget,

                previouslyReportedPartner = previousValues.previouslyReported.partnerContribution,
                previouslyReportedPublic = previousValues.previouslyReported.publicContribution,
                previouslyReportedAutoPublic = previousValues.previouslyReported.automaticPublicContribution,
                previouslyReportedPrivate = previousValues.previouslyReported.privateContribution,
                previouslyReportedSum = previousValues.previouslyReported.sum,

                previouslyVerifiedPartner = previousValues.previouslyVerified.partnerContribution,
                previouslyVerifiedPublic = previousValues.previouslyVerified.publicContribution,
                previouslyVerifiedAutoPublic = previousValues.previouslyVerified.automaticPublicContribution,
                previouslyVerifiedPrivate = previousValues.previouslyVerified.privateContribution,
                previouslyVerifiedSum = previousValues.previouslyVerified.sum,
            ).addExtraPaymentOnTopOfPreviouslyReported(cumulativePaymentValues)
        }

        private fun PreviouslyProjectReportedCoFinancing.addExtraPaymentOnTopOfPreviouslyReported(
            paymentLumpSums: ReportCertificateCoFinancingColumn
        ): PreviouslyProjectReportedCoFinancing {
            return this.copy(
                fundsSorted = fundsSorted.mergeWith(otherFundSums = paymentLumpSums.funds),
                previouslyReportedPartner = previouslyReportedPartner.plus(paymentLumpSums.partnerContribution),
                previouslyReportedPublic = previouslyReportedPublic.plus(paymentLumpSums.publicContribution),
                previouslyReportedAutoPublic = previouslyReportedAutoPublic.plus(paymentLumpSums.automaticPublicContribution),
                previouslyReportedPrivate = previouslyReportedPrivate.plus(paymentLumpSums.privateContribution),
                previouslyReportedSum = previouslyReportedSum.plus(paymentLumpSums.sum),

                previouslyVerifiedPartner = previouslyVerifiedPartner.plus(paymentLumpSums.partnerContribution),
                previouslyVerifiedPublic = previouslyVerifiedPublic.plus(paymentLumpSums.publicContribution),
                previouslyVerifiedAutoPublic = previouslyVerifiedAutoPublic.plus(paymentLumpSums.automaticPublicContribution),
                previouslyVerifiedPrivate = previouslyVerifiedPrivate.plus(paymentLumpSums.privateContribution),
                previouslyVerifiedSum = previouslyVerifiedSum.plus(paymentLumpSums.sum)
            )
        }

        private fun List<PreviouslyProjectReportedFund>.mergeWith(otherFundSums: Map<Long?, BigDecimal>) = map { previouslyReportedFund ->
            previouslyReportedFund.copy(
                previouslyReported = previouslyReportedFund.previouslyReported
                    .plus(otherFundSums.getOrDefault(previouslyReportedFund.fundId, ZERO)),
                previouslyVerified = previouslyReportedFund.previouslyVerified
                    .plus(otherFundSums.getOrDefault(previouslyReportedFund.fundId, ZERO))
            ) }
    }

    private fun Collection<ProjectLumpSum>.onlyReadyForPayment() = filter { it.isReady() }

    private fun Collection<ProjectLumpSum>.sumOfPaymentReady(): BigDecimal =
        onlyReadyForPayment()
            .flatMap { it.lumpSumContributions }
            .sumOf { it.amount }

    private fun CertificateCostCategoryPrevious.addExtraPaymentReadyFastTrackLumpSums(
        paymentReadyFastTrackLumpSums: BigDecimal,
    ): CertificateCostCategoryPrevious {
        return this.copy(
            previouslyReported = previouslyReported.copy(
                lumpSum = previouslyReported.lumpSum.plus(paymentReadyFastTrackLumpSums),
                sum = previouslyReported.sum.plus(paymentReadyFastTrackLumpSums)
            ),
            previouslyVerified = previouslyVerified.copy(
                lumpSum = previouslyVerified.lumpSum.plus(paymentReadyFastTrackLumpSums),
                sum = previouslyVerified.sum.plus(paymentReadyFastTrackLumpSums)
            )
        )
    }

    private fun getCostCategoryBreakdownFromAF(projectId: Long, version: String?): BudgetCostsCalculationResultFull {
        val budget = getProjectBudget.getBudget(projectId, version)
        return BudgetCostsCalculationResultFull(
            staff = budget.sumOf { it.staffCosts },
            office = budget.sumOf { it.officeAndAdministrationCosts },
            travel = budget.sumOf { it.travelCosts },
            external = budget.sumOf { it.externalCosts },
            equipment = budget.sumOf { it.equipmentCosts },
            infrastructure = budget.sumOf { it.infrastructureCosts },
            other = budget.sumOf { it.otherCosts },
            lumpSum = budget.sumOf { it.lumpSumContribution },
            unitCost = budget.sumOf { it.unitCosts },
            spfCost = budget.sumOf { it.spfCosts },
            sum = budget.sumOf { it.totalCosts },
        )
    }

    private fun List<ProjectLumpSum>.toProjectReportLumpSums(
        previouslyReported: Map<Int, BigDecimal>,
        previouslyVerified: Map<Int, BigDecimal>,
        previouslyPaid: Map<Long?, Map<Int?, BigDecimal>>,
    ) = map {
        val lumpSumPartnerShare = it.lumpSumContributions.sumOf { contribution -> contribution.amount }

        var fromPreviousReported = previouslyReported[it.orderNr] ?: ZERO
        var fromPreviousVerified = previouslyVerified[it.orderNr] ?: ZERO
        if (it.isReady()) {
            fromPreviousReported += lumpSumPartnerShare
            fromPreviousVerified += lumpSumPartnerShare
        }

        ProjectReportLumpSum(
            lumpSumId = it.programmeLumpSumId,
            orderNr = it.orderNr,
            period = it.period,
            total = lumpSumPartnerShare,
            previouslyReported = fromPreviousReported,
            previouslyPaid = previouslyPaid[it.programmeLumpSumId]?.get(it.orderNr) ?: ZERO,
            previouslyVerified = fromPreviousVerified
        )
    }

    private fun List<PaymentToProject>.byLumpSum() =
        groupBy { it.lumpSumId }
            .mapValues { (_, installments) ->
                installments.groupBy { it.orderNr }
                    .mapValues { (_, installments) -> installments.sumOf { it.amountPaidPerFund } }
            }

    private fun getSetOfUnitCostsWithTotalAndNumberOfUnits(
        budgetEntries: List<BaseBudgetEntry>,
        previouslyReported: Map<Long, BigDecimal>,
        previouslyVerified: Map<Long, BigDecimal>,
    ): Set<ProjectReportUnitCostBase> {
        return budgetEntries.filter { it.unitCostId != null }
            .groupBy { it.unitCostId!! }.entries
            .mapTo(HashSet()) { (unitCostId, budgetEntries) ->
                ProjectReportUnitCostBase(
                    unitCostId = unitCostId,
                    totalCost = budgetEntries.sumOf { it.rowSum!! },
                    numberOfUnits = budgetEntries.sumOf { it.numberOfUnits },
                    previouslyReported = previouslyReported[unitCostId] ?: ZERO,
                    previouslyVerified = previouslyVerified[unitCostId] ?: ZERO
                )
            }
    }

    private fun List<PartnerReportInvestmentSummary>.toPartnerReportInvestments(
        budgetEntries: List<BudgetGeneralCostEntry>,
        previouslyReported: Map<Long, BigDecimal>,
        previouslyVerified: Map<Long, BigDecimal>,
    ): List<ProjectReportInvestment> {
        val byInvestment = budgetEntries
            .filter { it.investmentId != null }
            .groupBy { it.investmentId!! }.mapValues { (_, entries) -> entries.sumOf { it.rowSum ?: ZERO } }

        return map {
            ProjectReportInvestment(
                investmentId = it.investmentId,
                investmentNumber = it.investmentNumber,
                workPackageNumber = it.workPackageNumber,
                title = it.title,
                deactivated = it.deactivated,
                total = byInvestment.getOrDefault(it.investmentId, ZERO),
                previouslyReported = previouslyReported[it.investmentId] ?: ZERO,
                previouslyVerified = previouslyVerified[it.investmentId] ?: ZERO
            )
        }
    }
}
