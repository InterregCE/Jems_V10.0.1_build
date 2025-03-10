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
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingWithSpfPart
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.AutomaticPublic
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.Private
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.Public
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportBudget
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PartnerReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedFund
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.create.ProjectPartnerReportContributionWithSpf
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.PartnerReportInvestmentSummary
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingPrevious
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.applyPercentage
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
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
    private val reportProjectPersistence: ProjectReportPersistence,
    private val reportProjectSpfClaimPersistence: ProjectReportSpfContributionClaimPersistence,
) {

    @Transactional(readOnly = true)
    fun retrieveBudgetDataFor(
        projectId: Long,
        partner: ProjectPartnerSummary,
        version: String?,
        coFinancing: PartnerReportCoFinancing,
        investments: List<PartnerReportInvestmentSummary>,
    ): PartnerReportBudget {
        val partnerId = partner.id!!
        val submittedPartnerReports = reportPersistence.getSubmittedPartnerReports(partnerId = partnerId)
        val submittedPartnerReportIds = submittedPartnerReports.mapTo(HashSet()) { it.reportId }
        val finalizedPartnerReportIds = submittedPartnerReports.filter { it.status.isFinalized() }
            .mapTo(HashSet()) { it.reportId }

        val submittedProjectReports = reportProjectPersistence.getSubmittedProjectReports(projectId)
        val submittedProjectReportIds =  submittedProjectReports.mapTo(HashSet()) { it.id }
        val finalizedProjectReportIds = submittedProjectReports.filter { it.status.isFinalized() }.mapTo(HashSet()) { it.id }

        val contributions = generateContributionsFromPreviousReports(
            submittedReportIds = submittedPartnerReportIds,
            partnerContributionsSorted = coFinancing.coFinancing.partnerContributions.sortedWith(compareBy({ it.isNotPartner() }, { it.id })),
            partnerContributionsSpfSorted = coFinancing.coFinancingSpf.partnerContributions.sortedWith(compareBy({ it.isNotPartner() }, { it.id })),
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

        val previouslyReportedSpf = reportProjectSpfClaimPersistence.getPreviouslyReportedSpfContributions(submittedProjectReportIds)
        val readyForPaymentLumpSums = paymentPersistence.getFtlsCumulativeForPartner(partnerId)

        val previouslyReportedCostCategories = getPreviouslyReportedCostCategories(
            partnerId,
            submittedReportIds = submittedPartnerReportIds,
            finalizedReportIds = finalizedPartnerReportIds,
            finalizedProjectReportIds = finalizedProjectReportIds,
            readyForPaymentLumpSums.sum,
            previouslyReportedSpf.sum
        )
        val partnerReportCoFinancingParkedValues = getPreviouslyReportedCoFinancingValues(
            partnerId,
            submittedPartnerReportIds = submittedPartnerReportIds,
            finalizedPartnerReportIds = finalizedPartnerReportIds,
            finalizedProjectReportIds = finalizedProjectReportIds
        )
        val previouslyReportedLumpSum = getPreviouslyReportedLumpSumsValues(partnerId, submittedPartnerReportIds, finalizedProjectReportIds)
        val previouslyReportedUnitCost = getPreviouslyReportedUnitCostsValues(partnerId, submittedPartnerReportIds, finalizedProjectReportIds)
        val previouslyReportedInvestment = getPreviouslyReportedInvestmentValues(partnerId, submittedPartnerReportIds, finalizedProjectReportIds)

        return PartnerReportBudget(
            contributions = contributions,
            availableLumpSums = lumpSums
                .toPartnerReportLumpSums(
                    partnerId = partnerId,
                    previouslyReported = previouslyReportedLumpSum,
                    previouslyPaid = installmentsPaid.byLumpSum(),
                    previouslyValidated = reportLumpSumPersistence.getLumpSumCumulativeAfterControl(finalizedPartnerReportIds)
                ),
            unitCosts = getSetOfUnitCostsWithTotalAndNumberOfUnits(
                staffCosts
                    .plus(travelCosts)
                    .plus(externalAndEquipmentAndInfrastructure)
                    .plus(unitCosts),
                previouslyReported = previouslyReportedUnitCost,
                previouslyValidated = reportUnitCostPersistence.getValidatedUnitCostCumulative(finalizedPartnerReportIds)
            ),
            investments = investments.toPartnerReportInvestments(
                budgetEntries = externalAndEquipmentAndInfrastructure,
                previouslyReported = previouslyReportedInvestment,
                previouslyValidated = reportInvestmentPersistence.getInvestmentsCumulativeAfterControl(finalizedPartnerReportIds)
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
            previouslyReportedCoFinancing = partnerReportCoFinancingParkedValues
                .toCreateModel(
                    coFinancing = coFinancing,
                    partnerTotal = budget.totalCosts,
                    partnerSpf = budget.spfCosts,
                    contributions = contributions,
                    previouslyReportedFastTrack = readyForPaymentLumpSums,
                    previouslyReportedSpf = previouslyReportedSpf,
                    paymentPaid = installmentsPaid.byFund(),
                ),
        )
    }

    private fun generateContributionsFromPreviousReports(
        submittedReportIds: Set<Long>,
        partnerContributionsSorted: List<ProjectPartnerContribution>,
        partnerContributionsSpfSorted: List<ProjectPartnerContributionSpf>,
    ) = ProjectPartnerReportContributionWithSpf(
        contributions = mergeAfContributionsWithReport(
            currentContributionsFromProject = partnerContributionsSorted,
            previouslyReportedContributions = reportContributionPersistence.getAllContributionsForReportIds(submittedReportIds),
        ),
        contributionsSpf = mergeAfContributionsWithReport(
            currentContributionsFromProject = partnerContributionsSpfSorted,
            previouslyReportedContributions = emptyList(),
        ),
    )

    private fun mergeAfContributionsWithReport(
        currentContributionsFromProject: List<ProjectContribution>,
        previouslyReportedContributions: List<ProjectPartnerReportEntityContribution>,
    ): List<CreateProjectPartnerReportContribution> {
        val projectContributionIdToUuid: MutableMap<Long, UUID> = mutableMapOf()
        val contributionsNotLinkedToApplicationForm: LinkedHashMap<UUID, Pair<String?, ProjectPartnerContributionStatus?>> = LinkedHashMap()
        val contributionsAll: MutableMap<UUID, MutableList<ProjectPartnerReportEntityContribution>> = mutableMapOf()

        previouslyReportedContributions.forEach {
            if (it.idFromApplicationForm != null)
                projectContributionIdToUuid[it.idFromApplicationForm] = it.historyIdentifier
            else
                contributionsNotLinkedToApplicationForm.putIfAbsent(it.historyIdentifier, it.toModel())

            contributionsAll.getOrPut(it.historyIdentifier) { mutableListOf() }
                .add(it)
        }

        val previouslyReported: Map<UUID, BigDecimal> = contributionsAll.mapValues { it.value.sumOf { it.currentlyReported } }

        val removedContributionUuids = projectContributionIdToUuid.keys
            .minus(currentContributionsFromProject.mapNotNullTo(HashSet()) { it.id })
            .map { projectContributionIdToUuid[it]!! }

        val contributionsExistingOnProject = currentContributionsFromProject
            .toContributions(projectContributionIdToUuid, previouslyReported = previouslyReported)
        val contributionsOnlyRelatedToReport = contributionsNotLinkedToApplicationForm
            .toContributions(previouslyReported = previouslyReported)
        val contributionsReportedPreviouslyButRemovedFromProject = removedContributionUuids
            .toContributions(contributionsAll)

        return buildList {
            addAll(contributionsExistingOnProject)
            addAll(contributionsOnlyRelatedToReport)
            addAll(contributionsReportedPreviouslyButRemovedFromProject)
        }
    }

    private fun ProjectPartnerReportEntityContribution.toModel() = Pair(sourceOfContribution, legalStatus)

    private fun List<ProjectContribution>.toContributions(
        idToUuid: Map<Long, UUID>,
        previouslyReported: Map<UUID, BigDecimal>,
    ) = map {
        val uuid = idToUuid[it.id] ?: UUID.randomUUID()
        return@map CreateProjectPartnerReportContribution(
            sourceOfContribution = it.name,
            legalStatus = it.status?.name?.let { ProjectPartnerContributionStatus.valueOf(it) },
            idFromApplicationForm = it.id,
            historyIdentifier = uuid,
            createdInThisReport = false,
            amount = it.amount ?: ZERO,
            previouslyReported = previouslyReported[uuid] ?: ZERO,
            currentlyReported = ZERO,
        )
    }

    private fun Map<UUID, Pair<String?, ProjectPartnerContributionStatus?>>.toContributions(
        previouslyReported: Map<UUID, BigDecimal>,
    ) = map { (uuid, formData) ->
        CreateProjectPartnerReportContribution(
            sourceOfContribution = formData.first,
            legalStatus = formData.second,
            idFromApplicationForm = null,
            historyIdentifier = uuid,
            createdInThisReport = false,
            amount = ZERO,
            previouslyReported = previouslyReported[uuid] ?: ZERO,
            currentlyReported = ZERO,
        )
    }

    private fun List<UUID>.toContributions(
        allExistingContributions: Map<UUID, MutableList<ProjectPartnerReportEntityContribution>>,
    ): List<CreateProjectPartnerReportContribution> {
        val previouslyReported = allExistingContributions.mapValues { it.value.sumOf { it.currentlyReported } }

        return map {
            val representative = allExistingContributions[it]!!.first()
            CreateProjectPartnerReportContribution(
                sourceOfContribution = representative.sourceOfContribution,
                legalStatus = representative.legalStatus,
                idFromApplicationForm = representative.idFromApplicationForm,
                historyIdentifier = it,
                createdInThisReport = false,
                amount = ZERO,
                previouslyReported = previouslyReported[it] ?: ZERO,
                currentlyReported = ZERO,
            )
        }
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
            spfCost = budget.spfCosts,
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
        spfCost = ZERO,
        sum = ZERO,
    )

    private fun BigDecimal.splitWith(coFinancing: ProjectCoFinancingAndContribution) =
        coFinancing.finances.filter { it.fundType == MainFund }
            .associateBy({ it.fund?.id }, { this.applyPercentage(it.percentage) })
            .toMutableMap()

    private fun ExpenditureCoFinancingPrevious.toCreateModel(
        coFinancing: PartnerReportCoFinancing,
        partnerTotal: BigDecimal,
        partnerSpf: BigDecimal,
        contributions: ProjectPartnerReportContributionWithSpf,
        previouslyReportedFastTrack: ReportExpenditureCoFinancingColumn,
        previouslyReportedSpf: ReportExpenditureCoFinancingColumn,
        paymentPaid: Map<Long, BigDecimal>,
    ): PreviouslyReportedCoFinancing {
        val partnerWithoutSpf = partnerTotal.minus(partnerSpf)
        val totals = partnerWithoutSpf.splitWith(coFinancing.coFinancing)
        val totalsSpf = partnerSpf.splitWith(coFinancing.coFinancingSpf)

        // partner contribution = total - all funds
        totals[null] = partnerWithoutSpf.minus(totals.values.sumOf { it })
        totalsSpf[null] = partnerSpf.minus(totalsSpf.values.sumOf { it })

        val currentFunds = coFinancing.mergeCoFinancing().mapTo(LinkedList()) {
            PreviouslyReportedFund(
                fundId = it.fundId,
                percentage = it.percentage,
                percentageSpf = it.percentageSpf,
                total = (totals[it.fundId] ?: ZERO).plus(totalsSpf[it.fundId] ?: ZERO),
                previouslyReported = previous.funds.getOrDefault(it.fundId, ZERO),
                previouslyReportedParked = previousParked.funds.getOrDefault(it.fundId, ZERO),
                previouslyReportedSpf = previouslyReportedSpf.funds.getOrDefault(it.fundId, ZERO),
                previouslyValidated = previousValidated.funds.getOrDefault(it.fundId, ZERO),
                previouslyPaid = paymentPaid.getOrDefault(it.fundId, ZERO),
                disabled = false,
            )
        }

        val previousFundIds = previous.funds.mapNotNullTo(HashSet()) { it.key } union
                previouslyReportedFastTrack.funds.mapNotNullTo(HashSet()) { it.key } union
                previouslyReportedSpf.funds.mapNotNullTo(HashSet()) { it.key }
        val currentFundIds = currentFunds.mapNotNullTo(HashSet()) { it.fundId }

        // in case in modification some funds have been removed, we still need it in reporting
        val removedFunds = previousFundIds.minus(currentFundIds).map { fundId ->
            PreviouslyReportedFund(
                fundId = fundId,
                percentage = ZERO,
                percentageSpf = ZERO,
                total = ZERO,
                previouslyReported = previous.funds.getOrDefault(fundId, ZERO),
                previouslyReportedParked = previousParked.funds.getOrDefault(fundId, ZERO),
                previouslyReportedSpf = previouslyReportedSpf.funds.getOrDefault(fundId, ZERO),
                previouslyValidated = previousValidated.funds.getOrDefault(fundId, ZERO),
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
                    percentageSpf = BigDecimal.valueOf(100),
                    total = ZERO,
                    previouslyReported = previous.funds.getOrDefault(null, ZERO),
                    previouslyReportedParked = previousParked.funds.getOrDefault(null, ZERO),
                    previouslyReportedSpf = previouslyReportedSpf.funds.getOrDefault(null, ZERO),
                    previouslyValidated = previousValidated.funds.getOrDefault(null, ZERO),
                    previouslyPaid = ZERO,
                    disabled = false,
                )
            )


        val publicTotalAmount = contributions.sumOf(Public)
        val autoPublicTotalAmount = contributions.sumOf(AutomaticPublic)
        val privateTotalAmount = contributions.sumOf(Private)

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

            previouslyReportedSpfPartner = previouslyReportedSpf.partnerContribution,
            previouslyReportedSpfPublic = previouslyReportedSpf.publicContribution,
            previouslyReportedSpfPrivate = previouslyReportedSpf.privateContribution,
            previouslyReportedSpfAutoPublic = previouslyReportedSpf.automaticPublicContribution,
            previouslyReportedSpfSum = previouslyReportedSpf.sum,

            previouslyValidatedPartner = previousValidated.partnerContribution,
            previouslyValidatedPublic = previousValidated.publicContribution,
            previouslyValidatedAutoPublic = previousValidated.automaticPublicContribution,
            previouslyValidatedPrivate = previousValidated.privateContribution,
            previouslyValidatedSum = previousValidated.sum,
        ).addExtraToPreviouslyReportedAndValidated(previouslyReportedFastTrack)
            .addExtraToPreviouslyReportedAndValidated(previouslyReportedSpf)
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

    private fun ExpenditureCostCategoryPreviouslyReportedWithParked.addVerificationParkedValues(verificationParked: BudgetCostsCalculationResultFull) =
        this.copy(previouslyReportedParked = this.previouslyReportedParked.sumParkedValues(verificationParked) )

    private fun ExpenditureCostCategoryPreviouslyReportedWithParked.addExtraSpfFromProjectReport(
        previousSpf: BigDecimal,
    ): ExpenditureCostCategoryPreviouslyReportedWithParked {
        return this.copy(
            previouslyReported = previouslyReported.copy(
                spfCost = previouslyReported.spfCost.plus(previousSpf),
                sum = previouslyReported.sum.plus(previousSpf),
            ),
            previouslyValidated = previouslyValidated.copy(
                spfCost = previouslyValidated.spfCost.plus(previousSpf),
                sum = previouslyValidated.sum.plus(previousSpf),
            )
        )
    }

    private fun PreviouslyReportedCoFinancing.addExtraToPreviouslyReportedAndValidated(
        paymentLumpSums: ReportExpenditureCoFinancingColumn,
    ): PreviouslyReportedCoFinancing {
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

    private fun PartnerReportCoFinancing.mergeCoFinancing(): List<ProjectPartnerCoFinancingWithSpfPart> {
        val regular = coFinancing.finances.associateBy { it.fund?.id }
        val spf = coFinancingSpf.finances.associateBy { it.fund?.id }

        val allFundIds = regular.keys
            .plus(spf.keys.minus(regular.keys))
        return allFundIds.map {
            ProjectPartnerCoFinancingWithSpfPart(
                fundId = it,
                percentage = regular[it]?.percentage ?: ZERO,
                percentageSpf = spf[it]?.percentage ?: ZERO,
            )
        }
    }

    private fun ProjectPartnerReportContributionWithSpf.sumOf(legalStatus: ProjectPartnerContributionStatus) =
        contributions.filter { it.legalStatus == legalStatus }.sumOf { it.amount }
            .plus(
                contributionsSpf.filter { it.legalStatus == legalStatus }.sumOf { it.amount }
            )

    private fun ExpenditureCoFinancingPrevious.addCoFinancingVerificationParkedValues(
        projectReportParkedValues: ReportCertificateCoFinancingColumn
    ): ExpenditureCoFinancingPrevious =
        this.copy(previousParked = this.previousParked.sumParkedValues(projectReportParkedValues))

   private fun ReportExpenditureCoFinancingColumn.sumParkedValues(projectReportParkedValues: ReportCertificateCoFinancingColumn) =
        ReportExpenditureCoFinancingColumn(
            funds = funds.mapValues { it.value.plus(projectReportParkedValues.funds[it.key] ?: ZERO) },
            partnerContribution = partnerContribution.plus(projectReportParkedValues.partnerContribution),
            publicContribution = publicContribution.plus(projectReportParkedValues.publicContribution),
            automaticPublicContribution = automaticPublicContribution.plus(projectReportParkedValues.automaticPublicContribution),
            privateContribution = privateContribution.plus(projectReportParkedValues.privateContribution),
            sum = sum.plus(projectReportParkedValues.sum)
        )

    private fun BudgetCostsCalculationResultFull.sumParkedValues(verificationParked: BudgetCostsCalculationResultFull) =
        BudgetCostsCalculationResultFull(
            staff = staff.plus(verificationParked.staff),
            office = office.plus(verificationParked.office),
            travel = travel.plus(verificationParked.travel),
            external = external.plus(verificationParked.external),
            equipment = equipment.plus(verificationParked.equipment),
            infrastructure = infrastructure.plus(verificationParked.infrastructure),
            other = other.plus(verificationParked.other),
            lumpSum = lumpSum.plus(verificationParked.lumpSum),
            unitCost = unitCost.plus(verificationParked.unitCost),
            spfCost = spfCost.plus(verificationParked.spfCost),
            sum = sum.plus(verificationParked.sum)
        )


    private fun Map<Int, ExpenditureLumpSumCurrent>.addLumpSumVerificationParkedValues(
        verificationParked: Map<Int, BigDecimal>
    ): Map<Int, ExpenditureLumpSumCurrent> =
        this.mapValues {
            ExpenditureLumpSumCurrent(
                current = it.value.current,
                currentParked = it.value.currentParked.plus(verificationParked[it.key] ?: ZERO)
            )
        }


    private fun  Map<Long, ExpenditureUnitCostCurrent>.addUnitCostVerificationParkedValues(verificationParked: Map<Long, BigDecimal>) =
        this.mapValues {
            ExpenditureUnitCostCurrent(
                current = it.value.current,
                currentParked = it.value.currentParked.plus(verificationParked[it.key] ?: ZERO)
            )
        }

    private fun  Map<Long, ExpenditureInvestmentCurrent>.addInvestmentVerificationParkedValues(verificationParked: Map<Long, BigDecimal>) =
        this.mapValues {
            ExpenditureInvestmentCurrent(
                current = it.value.current,
                currentParked = it.value.currentParked.plus(verificationParked[it.key] ?: ZERO)
            )
        }

    private fun getPreviouslyReportedLumpSumsValues(
        partnerId: Long,
        submittedReportIds: Set<Long>,
        finalizedProjectReportIds: Set<Long>
    ): Map<Int, ExpenditureLumpSumCurrent> {
        val projectReportParkedLumpSumValues = reportLumpSumPersistence.getCumulativeVerificationParked(partnerId, finalizedProjectReportIds)
        val previouslyReportedLumpSums = reportLumpSumPersistence.getLumpSumCumulative(submittedReportIds)
        return previouslyReportedLumpSums.addLumpSumVerificationParkedValues(projectReportParkedLumpSumValues)
    }

    private fun getPreviouslyReportedUnitCostsValues(
        partnerId: Long,
        submittedReportIds: Set<Long>,
        finalizedProjectReportIds: Set<Long>
    ): Map<Long, ExpenditureUnitCostCurrent> {
        val projectReportParkedUnitCostValues =
            reportUnitCostPersistence.getVerificationParkedUnitCostCumulative(partnerId, finalizedProjectReportIds)
        val previouslyReportedValues = reportUnitCostPersistence.getUnitCostCumulative(submittedReportIds)
        return previouslyReportedValues.addUnitCostVerificationParkedValues(projectReportParkedUnitCostValues)
    }

    private fun getPreviouslyReportedInvestmentValues(
        partnerId: Long,
        submittedReportIds: Set<Long>,
        finalizedProjectReportIds: Set<Long>
    ): Map<Long, ExpenditureInvestmentCurrent> {
        val projectReportParkedInvestmentValues = reportInvestmentPersistence.getVerificationParkedInvestmentsCumulative(partnerId, finalizedProjectReportIds)
        val previouslyReportedValues = reportInvestmentPersistence.getInvestmentsCumulative(submittedReportIds)
        return previouslyReportedValues.addInvestmentVerificationParkedValues(projectReportParkedInvestmentValues)
    }
    private fun getPreviouslyReportedCoFinancingValues(
        partnerId: Long,
        submittedPartnerReportIds: HashSet<Long>,
        finalizedPartnerReportIds: HashSet<Long>,
        finalizedProjectReportIds: HashSet<Long>
    ): ExpenditureCoFinancingPrevious {
        val verificationParkedValues =
            reportExpenditureCoFinancingPersistence.getVerificationParkedCoFinancingCumulative(
                partnerId,
                finalizedProjectReportIds
            )
        val previouslyReportedValues = reportExpenditureCoFinancingPersistence.getCoFinancingCumulative(
            submittedPartnerReportIds,
            finalizedPartnerReportIds
        )
        return previouslyReportedValues.addCoFinancingVerificationParkedValues(verificationParkedValues)
    }

    fun getPreviouslyReportedCostCategories(
        partnerId: Long,
        submittedReportIds: Set<Long>,
        finalizedReportIds: Set<Long>,
        finalizedProjectReportIds: Set<Long>,
        paymentReadyFastTrackLumpSums: BigDecimal,
        previouslyReportedSpfSum: BigDecimal
    ): ExpenditureCostCategoryPreviouslyReportedWithParked {
        val projectReportParkedCostCategories =
            reportExpenditureCostCategoryPersistence.getVerificationCostCategoriesCumulative(
                partnerId,
                finalizedProjectReportIds
            )
        val previouslyReportedCostCategories =
            reportExpenditureCostCategoryPersistence.getCostCategoriesCumulative(submittedReportIds, finalizedReportIds)

        return previouslyReportedCostCategories
            .addVerificationParkedValues(projectReportParkedCostCategories)
            .addExtraPaymentReadyFastTrackLumpSums(paymentReadyFastTrackLumpSums)
            .addExtraSpfFromProjectReport(previouslyReportedSpfSum)
    }

}
