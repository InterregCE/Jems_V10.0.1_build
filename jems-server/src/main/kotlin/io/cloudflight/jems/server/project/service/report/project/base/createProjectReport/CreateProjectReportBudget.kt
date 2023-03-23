package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.server.payments.service.regular.PaymentRegularPersistence
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
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
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedFund
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportBudget
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.project.base.create.ProjectReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryPreviouslyReported
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.applyPercentage
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.RoundingMode
import java.util.LinkedList

@Service
class CreateProjectReportBudget(
    private val reportPersistence: ProjectReportPersistence,
    private val lumpSumPersistence: ProjectLumpSumPersistence,
    private val getProjectBudget: GetProjectBudget,
    private val reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistenceProvider,
    private val paymentPersistence: PaymentRegularPersistence,
    private val reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistence,
    private val getPartnerBudgetPerFundService: GetPartnerBudgetPerFundService,
    private val reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence,
    private val partnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val projectBudgetPersistence: ProjectBudgetPersistence,
    private val reportCertificateUnitCostPersistence: ProjectReportCertificateUnitCostPersistence
) {

    @Transactional
    fun retrieveBudgetDataFor(
        projectId: Long,
        version: String?
    ): ProjectReportBudget {
        val submittedReportIds = reportPersistence.getSubmittedProjectReportIds(projectId = projectId)
            .mapTo(HashSet()) { it.first }

        val budget = getProjectBudget.getBudget(projectId, version)

        val partnerIds = projectBudgetPersistence.getPartnersForProjectId(projectId = projectId, version).map {it.id!!}.toSet()

        val staffCosts = partnerBudgetCostsPersistence.getBudgetStaffCosts(partnerIds, version)
        val travelCosts = partnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partnerIds, version)
        val externalAndEquipmentAndInfrastructure = partnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partnerIds, version)
            .plus(partnerBudgetCostsPersistence.getBudgetEquipmentCosts(partnerIds, version))
            .plus(partnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partnerIds, version))
        val unitCosts = partnerBudgetCostsPersistence.getBudgetUnitCosts(partnerIds, version)

        val lumpSums = lumpSumPersistence.getLumpSums(projectId, version = version)
        val sumOfPaymentReady = lumpSums.sumOfPaymentReady()

        val installmentsPaid = paymentPersistence.getPaymentsByProjectId(projectId)

        val previouslyReportedCostCategories = reportCertificateCostCategoryPersistence
            .getCostCategoriesCumulative(submittedReportIds)
            .addExtraPaymentReadyFastTrackLumpSums(sumOfPaymentReady)

        val totalsFromAF = getPartnerBudgetPerFundService.getProjectPartnerBudgetPerFund(projectId, version)
            .first { it.partner === null }
        val costCategoryBreakdownFromAF = getCostCategoryBreakdownFromAF(projectId, version)

        return ProjectReportBudget(
            coFinancing = reportCertificateCoFinancingPersistence
                .getCoFinancingCumulative(submittedReportIds)
                .toCreateModel(
                    totalsFromAF = totalsFromAF,
                    total = budget.sumOf { it.totalCosts },
                    paymentReadyFastTrackLumpSums = sumOfPaymentReady,
                    paymentPaid = installmentsPaid.byFund(),
                ),
            costCategorySetup = costCategorySetup(
                budget = costCategoryBreakdownFromAF,
                previouslyReported = previouslyReportedCostCategories
            ),
            availableLumpSums = lumpSums.toProjectReportLumpSums(
                previouslyReported = reportCertificateLumpSumPersistence.getLumpSumCumulative(submittedReportIds),
                previouslyPaid = installmentsPaid.byLumpSum()
            ),
            unitCosts = getSetOfUnitCostsWithTotalAndNumberOfUnits(
                staffCosts
                    .plus(travelCosts)
                    .plus(externalAndEquipmentAndInfrastructure)
                    .plus(unitCosts),
                previouslyReported = reportCertificateUnitCostPersistence.getUnitCostsCumulative(submittedReportIds)
            ),
        )
    }

    private fun costCategorySetup(
        budget: BudgetCostsCalculationResultFull,
        previouslyReported: CertificateCostCategoryPreviouslyReported,
    ) = ReportCertificateCostCategory(
        totalsFromAF = budget,
        currentlyReported = fillZeros(),
        previouslyReported = previouslyReported.previouslyReported,
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
        sum = ZERO,
    )

    private fun ProjectLumpSum.isReady() = fastTrack && readyForPayment

    private fun ReportCertificateCoFinancingColumn.toCreateModel(
        totalsFromAF: ProjectPartnerBudgetPerFund,
        total: BigDecimal,
        paymentReadyFastTrackLumpSums: BigDecimal,
        paymentPaid: Map<Long, BigDecimal>,
    ): PreviouslyProjectReportedCoFinancing {
        val totals = totalsFromAF.budgetPerFund.filter{ it.fund !== null }
            .associateBy({ it.fund?.id }, { it.value })
            .toMutableMap()

        // partner contribution = total - all funds
        totals[null] = total.minus(totals.values.sumOf { it })

        val currentFunds = totalsFromAF.budgetPerFund.filter{ it.fund !== null }.mapTo(LinkedList()) {
            PreviouslyProjectReportedFund(
                fundId = it.fund?.id,
                percentage = it.percentage,
                total = totals[it.fund?.id]!!,
                previouslyReported = funds.getOrDefault(it.fund?.id, ZERO),
                previouslyPaid = paymentPaid.getOrDefault(it.fund?.id, ZERO),
            )
        }

        // in case in modification some funds have been removed, we still need it in reporting
        val removedFunds = funds.mapNotNullTo(LinkedHashSet()) { it.key }.minus(
            currentFunds.mapNotNullTo(HashSet()) { it.fundId }
        ).map { fundId ->
            PreviouslyProjectReportedFund(
                fundId = fundId,
                percentage = ZERO,
                total = ZERO,
                previouslyReported = funds[fundId]!!,
                previouslyPaid = paymentPaid.getOrDefault(fundId, ZERO),
            )
        }
        currentFunds.addAll(maxOf(currentFunds.size - 1, 0), removedFunds) /* insert removed funds before partner contribution */

        // if co-financing has not been filled-in in Application Form, mock Partner contribution line
        if (currentFunds.isEmpty())
            currentFunds.add(
                PreviouslyProjectReportedFund(
                    fundId = null,
                    percentage = BigDecimal.valueOf(100),
                    total = ZERO,
                    previouslyReported = funds.getOrDefault(null, ZERO),
                    previouslyPaid = ZERO,
                )
            )

        val publicTotalAmount = totalsFromAF.publicContribution ?: ZERO
        val autoPublicTotalAmount = totalsFromAF.autoPublicContribution ?: ZERO
        val privateTotalAmount = totalsFromAF.privateContribution ?: ZERO

        val partnerContributionsFund = PreviouslyProjectReportedFund(
            fundId = null,
            percentage = BigDecimal.valueOf(100).minus(currentFunds.sumOf { it.percentage }),
            total = publicTotalAmount.plus(autoPublicTotalAmount).plus(privateTotalAmount),
            previouslyReported = partnerContribution,
            previouslyPaid = ZERO
        )

        // calculate lump sums values to be added from payments
        val currentFundsPercentages = currentFunds.associateBy ({it.fundId}, {it.percentage })
        val partnerContributionPercentage = BigDecimal.valueOf(100).minus(currentFunds.sumOf { it.percentage })

        val currentLumpSumValues = ReportCertificateCoFinancingColumn(
            funds = currentFundsPercentages.mapValues { fundPercentage -> paymentReadyFastTrackLumpSums.applyPercentage(fundPercentage.value) }
                .plus(Pair(null, paymentReadyFastTrackLumpSums.applyPercentage(partnerContributionPercentage))),
            partnerContribution = paymentReadyFastTrackLumpSums.applyPercentage(partnerContributionPercentage),
            publicContribution = paymentReadyFastTrackLumpSums.applyPercentage(publicTotalAmount.getPercentageOf(total)),
            automaticPublicContribution = paymentReadyFastTrackLumpSums.applyPercentage(autoPublicTotalAmount.getPercentageOf(total)),
            privateContribution = paymentReadyFastTrackLumpSums.applyPercentage(privateTotalAmount.getPercentageOf(total)),
            sum = paymentReadyFastTrackLumpSums
        )

        //add one extra fund to generate the partner contribution total
        currentFunds.add(partnerContributionsFund)

        return PreviouslyProjectReportedCoFinancing(
            fundsSorted = currentFunds,

            totalPartner = publicTotalAmount.plus(autoPublicTotalAmount).plus(privateTotalAmount),
            totalPublic = publicTotalAmount,
            totalAutoPublic = autoPublicTotalAmount,
            totalPrivate = privateTotalAmount,
            totalSum = total,

            previouslyReportedPartner = partnerContribution,
            previouslyReportedPublic = publicContribution,
            previouslyReportedAutoPublic = automaticPublicContribution,
            previouslyReportedPrivate = privateContribution,
            previouslyReportedSum = sum
        ).addExtraLumpSumValues(currentLumpSumValues)
    }

    private fun List<PaymentToProject>.byFund() =
        groupBy { it.fundId }
            .mapValues { (_, payments) -> payments.sumOf { it.amountPaidPerFund } }

    private fun Collection<ProjectLumpSum>.onlyReadyForPayment() = filter { it.isReady() }

    private fun Collection<ProjectLumpSum>.sumOfPaymentReady(): BigDecimal =
        onlyReadyForPayment()
            .flatMap { it.lumpSumContributions }
            .sumOf { it.amount }

    private fun PreviouslyProjectReportedCoFinancing.addExtraLumpSumValues(
        paymentLumpSums: ReportCertificateCoFinancingColumn
    ): PreviouslyProjectReportedCoFinancing {
        return this.copy(
            fundsSorted = fundsSorted.mergeWith(otherFundSums = paymentLumpSums.funds),
            previouslyReportedPartner = previouslyReportedPartner.plus(paymentLumpSums.partnerContribution),
            previouslyReportedPublic = previouslyReportedPublic.plus(paymentLumpSums.publicContribution),
            previouslyReportedAutoPublic = previouslyReportedAutoPublic.plus(paymentLumpSums.automaticPublicContribution),
            previouslyReportedPrivate = previouslyReportedPrivate.plus(paymentLumpSums.privateContribution),
            previouslyReportedSum = previouslyReportedSum.plus(paymentLumpSums.sum),
        )
    }

    private fun List<PreviouslyProjectReportedFund>.mergeWith(otherFundSums: Map<Long?, BigDecimal>) = map { previouslyReportedFund ->
        previouslyReportedFund.copy(
            previouslyReported = previouslyReportedFund.previouslyReported
                .plus(otherFundSums.getOrDefault(previouslyReportedFund.fundId, ZERO))
        ) }

    private fun BigDecimal.getPercentageOf(total: BigDecimal) =
        if (total.compareTo(ZERO) == 0)
            ZERO
        else
            this.multiply(BigDecimal.valueOf(100))
                .divide(total, 17, RoundingMode.DOWN)

    private fun CertificateCostCategoryPreviouslyReported.addExtraPaymentReadyFastTrackLumpSums(
        paymentReadyFastTrackLumpSums: BigDecimal,
    ): CertificateCostCategoryPreviouslyReported {
        return this.copy(
            previouslyReported = previouslyReported.copy(
                lumpSum = previouslyReported.lumpSum.plus(paymentReadyFastTrackLumpSums),
                sum = previouslyReported.sum.plus(paymentReadyFastTrackLumpSums)
            ),
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
            sum = budget.sumOf { it.totalCosts },
        )
    }

    private fun List<ProjectLumpSum>.toProjectReportLumpSums(
        previouslyReported: Map<Int, BigDecimal>,
        previouslyPaid: Map<Long?, Map<Int?, BigDecimal>>,
    ) = map {
        val lumpSumPartnerShare = it.lumpSumContributions.sumOf { contribution -> contribution.amount }

        var fromPrevious = previouslyReported.get(it.orderNr) ?: ZERO
        if (it.isReady()) {
            fromPrevious += lumpSumPartnerShare
        }

        ProjectReportLumpSum(
            lumpSumId = it.programmeLumpSumId,
            orderNr = it.orderNr,
            period = it.period,
            total = lumpSumPartnerShare,
            previouslyReported = fromPrevious,
            previouslyPaid = previouslyPaid.get(it.programmeLumpSumId)?.get(it.orderNr) ?: ZERO,
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
    ): Set<ProjectReportUnitCostBase> {
        return budgetEntries.filter { it.unitCostId != null }
            .groupBy { it.unitCostId!! }.entries
            .mapTo(HashSet()) { (unitCostId, budgetEntries) ->
                ProjectReportUnitCostBase(
                    unitCostId = unitCostId,
                    totalCost = budgetEntries.sumOf { it.rowSum!! },
                    numberOfUnits = budgetEntries.sumOf { it.numberOfUnits },
                    previouslyReported = previouslyReported.get(unitCostId) ?: ZERO,
                )
            }
    }
}
