package io.cloudflight.jems.server.project.service.report.partner.base.createProjectPartnerReport

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.MainFund
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerInstallment
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryPreviouslyReportedWithParked
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.AutomaticPublic
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.Private
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.Public
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportBudget
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedFund
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.PartnerReportInvestmentSummary
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingPrevious
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.applyPercentage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.util.LinkedList
import java.util.UUID

@Service
class CreateProjectPartnerReportBudget(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportContributionPersistence: ProjectPartnerReportContributionPersistence,
    private val lumpSumPersistence: ProjectLumpSumPersistence,
    private val partnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val getPartnerBudgetPerPeriod: GetPartnerBudgetPerPeriodInteractor,
    private val projectPartnerBudgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val getProjectBudget: GetProjectBudget,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val paymentPersistence: PaymentPersistence,
    private val reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence,
    private val reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence,
    private val reportInvestmentPersistence: ProjectPartnerReportInvestmentPersistence,
) {

    @Transactional
    fun retrieveBudgetDataFor(
        projectId: Long,
        partner: ProjectPartnerSummary,
        version: String?,
        coFinancing: ProjectPartnerCoFinancingAndContribution,
        investments: List<PartnerReportInvestmentSummary>,
    ): PartnerReportBudget {
        val partnerId = partner.id!!
        val submittedReports = reportPersistence.getSubmittedPartnerReports(partnerId = partnerId)
        val submittedReportIds = submittedReports.mapTo(HashSet()) { it.reportId }
        val finalizedReportIds = submittedReports.filter { it.status.isFinalized() }
            .mapTo(HashSet()) { it.reportId }

        val contributions = generateContributionsFromPreviousReports(
            submittedReportIds = submittedReportIds,
            partnerContributionsSorted = coFinancing.partnerContributions.sortedWith(compareBy({ it.isNotPartner() }, { it.id })),
        )
        val budget = getProjectBudget.getBudget(listOf(partner), projectId, version).first()

        val lumpSums = lumpSumPersistence.getLumpSums(projectId, version = version)

        val staffCosts = partnerBudgetCostsPersistence.getBudgetStaffCosts(setOf(partnerId), version)
        val travelCosts = partnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(setOf(partnerId), version)
        val externalAndEquipmentAndInfrastructure = partnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(setOf(partnerId), version)
            .plus(partnerBudgetCostsPersistence.getBudgetEquipmentCosts(setOf(partnerId), version))
            .plus(partnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(setOf(partnerId), version))
        val unitCosts = partnerBudgetCostsPersistence.getBudgetUnitCosts(setOf(partnerId), version)

        val installmentsPaid = paymentPersistence.findByPartnerId(partnerId).getOnlyPaid()

        val readyForPaymentLumpSums = paymentPersistence.getCoFinancingAndContributionsCumulative(partnerId)
        val previouslyReportedCostCategories = reportExpenditureCostCategoryPersistence
            .getCostCategoriesCumulative(submittedReportIds, finalizedReportIds)
            .addExtraPaymentReadyFastTrackLumpSums(readyForPaymentLumpSums.sum)

        return PartnerReportBudget(
            contributions = contributions,
            availableLumpSums = lumpSums
                .toPartnerReportLumpSums(
                    partnerId = partnerId,
                    previouslyReported = reportLumpSumPersistence.getLumpSumCumulative(submittedReportIds),
                    previouslyPaid = installmentsPaid.byLumpSum(),
                    previouslyValidated = reportLumpSumPersistence.getLumpSumCumulativeAfterControl(finalizedReportIds)
                ),
            unitCosts = getSetOfUnitCostsWithTotalAndNumberOfUnits(
                staffCosts
                    .plus(travelCosts)
                    .plus(externalAndEquipmentAndInfrastructure)
                    .plus(unitCosts),
                previouslyReported = reportUnitCostPersistence.getUnitCostCumulative(submittedReportIds),
                previouslyValidated = reportUnitCostPersistence.getValidatedUnitCostCumulative(finalizedReportIds)
            ),
            investments = investments.toPartnerReportInvestments(
                budgetEntries = externalAndEquipmentAndInfrastructure,
                previouslyReported = reportInvestmentPersistence.getInvestmentsCumulative(submittedReportIds),
                previouslyValidated = reportInvestmentPersistence.getInvestmentsCumulativeAfterControl(finalizedReportIds)
            ),
            budgetPerPeriod = (
                getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId = projectId, version)
                    .partnersBudgetPerPeriod.firstOrNull { it.partner.id == partnerId }?.periodBudgets ?: emptyList()
                ).getCumulative(),
            expenditureSetup = expenditureSetup(
                options = projectPartnerBudgetOptionsPersistence.getBudgetOptions(partnerId, version) ?: ProjectPartnerBudgetOptions(partnerId),
                budget = budget,
                previouslyReportedWithParked = previouslyReportedCostCategories,
             ),
            previouslyReportedCoFinancing = reportExpenditureCoFinancingPersistence
                .getCoFinancingCumulative(submittedReportIds, finalizedReportIds)
                .toCreateModel(
                    coFinancing = coFinancing,
                    partnerTotal = budget.totalCosts,
                    contributions = contributions,
                    previouslyReportedFastTrack = readyForPaymentLumpSums,
                    paymentPaid = installmentsPaid.byFund(),
                ),
        )
    }

    private fun generateContributionsFromPreviousReports(
        submittedReportIds: Set<Long>,
        partnerContributionsSorted: List<ProjectPartnerContribution>,
    ): List<CreateProjectPartnerReportContribution> {
        val mapIdToHistoricalIdentifier: MutableMap<Long, UUID> = mutableMapOf()
        val contributionsNotLinkedToApplicationForm: LinkedHashMap<UUID, Pair<String?, ProjectPartnerContributionStatus?>> = LinkedHashMap()
        val historicalContributions: MutableMap<UUID, MutableList<BigDecimal>> = mutableMapOf()

        reportContributionPersistence.getAllContributionsForReportIds(reportIds = submittedReportIds).forEach {
            if (it.idFromApplicationForm != null)
                mapIdToHistoricalIdentifier[it.idFromApplicationForm] = it.historyIdentifier
            else
                contributionsNotLinkedToApplicationForm.putIfAbsent(it.historyIdentifier, it.toModel())

            historicalContributions.getOrPut(it.historyIdentifier) { mutableListOf() }
                .add(it.currentlyReported)
        }

        return partnerContributionsSorted
            .fromApplicationForm(
                idToUuid = mapIdToHistoricalIdentifier,
                historicalContributions = historicalContributions
            )
            .plus(
                contributionsNotLinkedToApplicationForm
                    .accumulatePreviousContributions(historicalContributions = historicalContributions)
            )
    }

    private fun ProjectPartnerReportEntityContribution.toModel() = Pair(sourceOfContribution, legalStatus)

    private fun List<ProjectPartnerContribution>.fromApplicationForm(
        idToUuid: Map<Long, UUID>,
        historicalContributions: Map<UUID, MutableList<BigDecimal>>,
    ) = filter { it.id != null }.map {
        (idToUuid[it.id] ?: UUID.randomUUID()).let { uuid ->
            CreateProjectPartnerReportContribution(
                sourceOfContribution = it.name,
                legalStatus = it.status?.name?.let { ProjectPartnerContributionStatus.valueOf(it) },
                idFromApplicationForm = it.id,
                historyIdentifier = uuid,
                createdInThisReport = false,
                amount = it.amount ?: ZERO,
                previouslyReported = historicalContributions[uuid]?.sumOf { it } ?: ZERO,
                currentlyReported = ZERO,
            )
        }
    }

    private fun Map<UUID, Pair<String?, ProjectPartnerContributionStatus?>>.accumulatePreviousContributions(
        historicalContributions: Map<UUID, MutableList<BigDecimal>>,
    ) = map { (uuid, formData) ->
        CreateProjectPartnerReportContribution(
            sourceOfContribution = formData.first,
            legalStatus = formData.second,
            idFromApplicationForm = null,
            historyIdentifier = uuid,
            createdInThisReport = false,
            amount = ZERO,
            previouslyReported = historicalContributions[uuid]?.sumOf { it } ?: ZERO,
            currentlyReported = ZERO,
        )
    }

    private fun List<PartnerReportInvestmentSummary>.toPartnerReportInvestments(
        budgetEntries: List<BudgetGeneralCostEntry>,
        previouslyReported: Map<Long, ExpenditureInvestmentCurrent>,
        previouslyValidated: Map<Long, BigDecimal>
    ): List<PartnerReportInvestment> {
        val byInvestment = budgetEntries
            .filter { it.investmentId != null }
            .groupBy { it.investmentId!! }.mapValues { (_, entries) -> entries.sumOf { it.rowSum ?: ZERO } }

        return map {
            PartnerReportInvestment(
                investmentId = it.investmentId,
                investmentNumber = it.investmentNumber,
                workPackageNumber = it.workPackageNumber,
                title = it.title,
                deactivated = it.deactivated,
                total = byInvestment.getOrDefault(it.investmentId, ZERO),
                previouslyReported = previouslyReported.get(it.investmentId)?.current ?: ZERO,
                previouslyReportedParked = previouslyReported.get(it.investmentId)?.currentParked ?: ZERO,
                previouslyValidated = previouslyValidated.get(it.investmentId) ?: ZERO,
            )
        }
    }

    private fun List<ProjectLumpSum>.toPartnerReportLumpSums(
        partnerId: Long,
        previouslyReported: Map<Int, ExpenditureLumpSumCurrent>,
        previouslyPaid: Map<Long, Map<Int, BigDecimal>>,
        previouslyValidated: Map<Int, BigDecimal>
    ) = map {
        val lumpSumPartnerShare = it.lumpSumContributions.firstOrNull { it.partnerId == partnerId }?.amount ?: ZERO

        var fromPrevious = previouslyReported.get(it.orderNr)?.current ?: ZERO
        var previouslyValidatedSum =  previouslyValidated.get(it.orderNr) ?: ZERO
        if (it.isReady()) {
            fromPrevious += lumpSumPartnerShare
            previouslyValidatedSum += lumpSumPartnerShare
        }

        PartnerReportLumpSum(
            lumpSumId = it.programmeLumpSumId,
            orderNr = it.orderNr,
            period = it.period,
            total = lumpSumPartnerShare,
            previouslyReported = fromPrevious,
            previouslyReportedParked = previouslyReported.get(it.orderNr)?.currentParked ?: ZERO,
            previouslyValidated = previouslyValidatedSum,
            previouslyPaid = previouslyPaid.get(it.programmeLumpSumId)?.get(it.orderNr) ?: ZERO,
        )
    }

    private fun ProjectLumpSum.isReady() = fastTrack && readyForPayment

    private fun getSetOfUnitCostsWithTotalAndNumberOfUnits(
        budgetEntries: List<BaseBudgetEntry>,
        previouslyReported: Map<Long, ExpenditureUnitCostCurrent>,
        previouslyValidated: Map<Long, BigDecimal>
    ): Set<PartnerReportUnitCostBase> {
        return budgetEntries.filter { it.unitCostId != null }
            .groupBy { it.unitCostId!! }.entries
            .mapTo(HashSet()) { (unitCostId, budgetEntries) ->
                PartnerReportUnitCostBase(
                    unitCostId = unitCostId,
                    totalCost = budgetEntries.sumOf { it.rowSum!! },
                    numberOfUnits = budgetEntries.sumOf { it.numberOfUnits },
                    previouslyReported = previouslyReported.get(unitCostId)?.current ?: ZERO,
                    previouslyReportedParked = previouslyReported.get(unitCostId)?.currentParked ?: ZERO,
                    previouslyValidated = previouslyValidated.get(unitCostId) ?: ZERO,
                )
            }
    }

    private fun List<ProjectPeriodBudget>.getCumulative() = sortedBy { it.periodNumber }
        .fold(emptyList<ProjectPartnerReportPeriod>()) { previous, current ->
            previous.plus(
                ProjectPartnerReportPeriod(
                    number = current.periodNumber,
                    periodBudget = current.totalBudgetPerPeriod,
                    periodBudgetCumulative = current.totalBudgetPerPeriod
                        .plus(previous.lastOrNull()?.periodBudgetCumulative ?: ZERO),
                    start = current.periodStart,
                    end = current.periodEnd,
                )
            )
        }

    private fun expenditureSetup(
        options: ProjectPartnerBudgetOptions,
        budget: PartnerBudget,
        previouslyReportedWithParked: ExpenditureCostCategoryPreviouslyReportedWithParked,
    ) = ReportExpenditureCostCategory(
        options = options,
        totalsFromAF = BudgetCostsCalculationResultFull(
            staff = budget.staffCosts,
            office = budget.officeAndAdministrationCosts,
            travel = budget.travelCosts,
            external = budget.externalCosts,
            equipment = budget.equipmentCosts,
            infrastructure = budget.infrastructureCosts,
            other = budget.otherCosts,
            lumpSum = budget.lumpSumContribution,
            unitCost = budget.unitCosts,
            sum = budget.totalCosts,
        ),
        currentlyReported = fillZeros(),
        totalEligibleAfterControl = fillZeros(),
        previouslyReported = previouslyReportedWithParked.previouslyReported,
        currentlyReportedParked = fillZeros(),
        currentlyReportedReIncluded = fillZeros(),
        previouslyReportedParked = previouslyReportedWithParked.previouslyReportedParked,
        previouslyValidated = previouslyReportedWithParked.previouslyValidated,
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

    private fun ExpenditureCoFinancingPrevious.toCreateModel(
        coFinancing: ProjectPartnerCoFinancingAndContribution,
        partnerTotal: BigDecimal,
        contributions: List<CreateProjectPartnerReportContribution>,
        previouslyReportedFastTrack: ReportExpenditureCoFinancingColumn,
        paymentPaid: Map<Long, BigDecimal>,
    ): PreviouslyReportedCoFinancing {
        val totals = coFinancing.finances.filter { it.fundType == MainFund }
            .associateBy({ it.fund?.id }, { partnerTotal.applyPercentage(it.percentage) })
            .toMutableMap()

        // partner contribution = total - all funds
        totals[null] = partnerTotal.minus(totals.values.sumOf { it })

        val currentFunds = coFinancing.finances.mapTo(LinkedList()) {
            PreviouslyReportedFund(
                fundId = it.fund?.id,
                percentage = it.percentage,
                total = totals[it.fund?.id]!!,
                previouslyReported = previous.funds.getOrDefault(it.fund?.id, ZERO),
                previouslyReportedParked = previousParked.funds.getOrDefault(it.fund?.id, ZERO),
                previouslyValidated = previousValidated.funds.getOrDefault(it.fund?.id, ZERO),
                previouslyPaid = paymentPaid.getOrDefault(it.fund?.id, ZERO),
                disabled = false,
            )
        }

        // in case in modification some funds have been removed, we still need it in reporting
        val removedFunds = previous.funds.mapNotNullTo(LinkedHashSet()) { it.key }.minus(
            currentFunds.mapNotNullTo(HashSet()) { it.fundId }
        ).map { fundId ->
            PreviouslyReportedFund(
                fundId = fundId,
                percentage = ZERO,
                total = ZERO,
                previouslyReported = previous.funds[fundId]!!,
                previouslyReportedParked = previousParked.funds[fundId]!!,
                previouslyValidated = previousValidated.funds[fundId]!!,
                previouslyPaid = paymentPaid.getOrDefault(fundId, ZERO),
                disabled = true,
            )
        }
        currentFunds.addAll(maxOf(currentFunds.size - 1, 0), removedFunds) /* insert removed funds before partner contribution */

        // if co-financing has not been filled-in in Application Form, mock Partner contribution line
        if (currentFunds.isEmpty())
            currentFunds.add(
                PreviouslyReportedFund(
                    fundId = null,
                    percentage = BigDecimal.valueOf(100),
                    total = ZERO,
                    previouslyReported = previous.funds.getOrDefault(null, ZERO),
                    previouslyReportedParked = previousParked.funds.getOrDefault(null, ZERO),
                    previouslyValidated = previousValidated.funds.getOrDefault(null, ZERO),
                    previouslyPaid = ZERO,
                    disabled = false,
                )
            )


        val publicTotalAmount = contributions.filter { it.legalStatus == Public }.sumOf { it.amount }
        val autoPublicTotalAmount = contributions.filter { it.legalStatus == AutomaticPublic }.sumOf { it.amount }
        val privateTotalAmount = contributions.filter { it.legalStatus == Private }.sumOf { it.amount }

        return PreviouslyReportedCoFinancing(
            fundsSorted = currentFunds,

            totalPartner = publicTotalAmount.plus(autoPublicTotalAmount).plus(privateTotalAmount),
            totalPublic = publicTotalAmount,
            totalAutoPublic = autoPublicTotalAmount,
            totalPrivate = privateTotalAmount,
            totalSum = partnerTotal,

            previouslyReportedPartner = previous.partnerContribution,
            previouslyReportedPublic = previous.publicContribution,
            previouslyReportedAutoPublic = previous.automaticPublicContribution,
            previouslyReportedPrivate = previous.privateContribution,
            previouslyReportedSum = previous.sum,

            previouslyReportedParkedPartner = previousParked.partnerContribution,
            previouslyReportedParkedPublic = previousParked.publicContribution,
            previouslyReportedParkedPrivate = previousParked.privateContribution,
            previouslyReportedParkedAutoPublic = previousParked.automaticPublicContribution,
            previouslyReportedParkedSum = previousParked.sum,

            previouslyValidatedPartner = previousValidated.partnerContribution,
            previouslyValidatedPublic = previousValidated.publicContribution,
            previouslyValidatedAutoPublic = previousValidated.automaticPublicContribution,
            previouslyValidatedPrivate = previousValidated.privateContribution,
            previouslyValidatedSum = previousValidated.sum,
        ).addExtraLumpSumValues(previouslyReportedFastTrack)
    }

    private fun List<PaymentPartnerInstallment>.byFund() =
        groupBy { it.fundId }
            .mapValues { (_, installments) -> installments.sumOf { it.amountPaid ?: ZERO } }

    private fun List<PaymentPartnerInstallment>.byLumpSum() =
        filter { it.lumpSumId != null }.groupBy { it.lumpSumId!! }
            .mapValues { (_, installments) ->
                installments.groupBy { it.orderNr!! }
                    .mapValues { (_, installments) -> installments.sumOf { it.amountPaid ?: ZERO } }
            }

    private fun List<PaymentPartnerInstallment>.getOnlyPaid() =
        filter { it.isPaymentConfirmed!! }

    private fun ExpenditureCostCategoryPreviouslyReportedWithParked.addExtraPaymentReadyFastTrackLumpSums(
        paymentReadyFastTrackLumpSums: BigDecimal,
    ): ExpenditureCostCategoryPreviouslyReportedWithParked {
        return this.copy(
            previouslyReported = previouslyReported.copy(
                lumpSum = previouslyReported.lumpSum.plus(paymentReadyFastTrackLumpSums),
                sum = previouslyReported.sum.plus(paymentReadyFastTrackLumpSums)
            ),
            previouslyValidated = previouslyValidated.copy(
                lumpSum = previouslyValidated.lumpSum.plus(paymentReadyFastTrackLumpSums),
                sum = previouslyValidated.sum.plus(paymentReadyFastTrackLumpSums)
            )
        )
    }

    private fun PreviouslyReportedCoFinancing.addExtraLumpSumValues(paymentLumpSums: ReportExpenditureCoFinancingColumn): PreviouslyReportedCoFinancing {
        return this.copy(
            fundsSorted = fundsSorted.mergeWith(otherFundSums = paymentLumpSums.funds),
            previouslyReportedPartner = previouslyReportedPartner.plus(paymentLumpSums.partnerContribution),
            previouslyReportedPublic = previouslyReportedPublic.plus(paymentLumpSums.publicContribution),
            previouslyReportedAutoPublic = previouslyReportedAutoPublic.plus(paymentLumpSums.automaticPublicContribution),
            previouslyReportedPrivate = previouslyReportedPrivate.plus(paymentLumpSums.privateContribution),
            previouslyReportedSum = previouslyReportedSum.plus(paymentLumpSums.sum),

            previouslyValidatedPartner = previouslyValidatedPartner.plus(paymentLumpSums.partnerContribution),
            previouslyValidatedPublic = previouslyValidatedPublic.plus(paymentLumpSums.publicContribution),
            previouslyValidatedAutoPublic = previouslyValidatedAutoPublic.plus(paymentLumpSums.automaticPublicContribution),
            previouslyValidatedPrivate = previouslyValidatedPrivate.plus(paymentLumpSums.privateContribution),
            previouslyValidatedSum = previouslyValidatedSum.plus(paymentLumpSums.sum)
        )
    }

    private fun List<PreviouslyReportedFund>.mergeWith(otherFundSums: Map<Long?, BigDecimal>) = map { previouslyReportedFund ->
        previouslyReportedFund.copy(
            previouslyReported = previouslyReportedFund.previouslyReported
                .plus(otherFundSums.getOrDefault(previouslyReportedFund.fundId, ZERO)),
            previouslyValidated = previouslyReportedFund.previouslyValidated
                .plus(otherFundSums.getOrDefault(previouslyReportedFund.fundId, ZERO))
        )
    }

}
