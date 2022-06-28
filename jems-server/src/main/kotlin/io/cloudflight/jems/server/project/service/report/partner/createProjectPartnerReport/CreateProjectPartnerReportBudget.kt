package io.cloudflight.jems.server.project.service.report.partner.createProjectPartnerReport

import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportBudget
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportUnitCostBase
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCostCategoryPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.util.UUID

@Service
class CreateProjectPartnerReportBudget(
    private val reportPersistence: ProjectReportPersistence,
    private val reportContributionPersistence: ProjectReportContributionPersistence,
    private val lumpSumPersistence: ProjectLumpSumPersistence,
    private val partnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val getPartnerBudgetPerPeriod: GetPartnerBudgetPerPeriodInteractor,
    private val projectPartnerBudgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val getProjectBudget: GetProjectBudget,
    private val reportExpenditureCostCategoryPersistence: ProjectReportExpenditureCostCategoryPersistence
) {

    @Transactional
    fun retrieveBudgetDataFor(
        projectId: Long,
        partner: ProjectPartnerSummary,
        version: String?,
        partnerContributions: Collection<ProjectPartnerContribution>,
    ): PartnerReportBudget {
        val partnerId = partner.id!!
        val submittedReportIds = reportPersistence.listSubmittedPartnerReports(partnerId = partnerId).mapTo(HashSet()) { it.id }

        return PartnerReportBudget(
            contributions = generateContributionsFromPreviousReports(
                submittedReportIds = submittedReportIds,
                partnerContributionsSorted = partnerContributions.sortedWith(compareBy({ it.isNotPartner() }, { it.id })),
            ),
            lumpSums = lumpSumPersistence.getLumpSums(projectId, version = version)
                .filter { it.isFastTrack != null && !it.isFastTrack }
                .toPartnerReportLumpSums(partnerId = partnerId),
            unitCosts = getSetOfUnitCostsWithTotalAndNumberOfUnits(
                partnerBudgetCostsPersistence.getBudgetStaffCosts(partnerId, version)
                    .asSequence()
                    .plus(partnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partnerId, version))
                    .plus(partnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partnerId, version))
                    .plus(partnerBudgetCostsPersistence.getBudgetEquipmentCosts(partnerId, version))
                    .plus(partnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partnerId, version))
                    .plus(partnerBudgetCostsPersistence.getBudgetUnitCosts(partnerId, version))
                    .toList()),
            budgetPerPeriod = (
                getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId = projectId, version)
                    .partnersBudgetPerPeriod.firstOrNull { it.partner.id == partnerId }?.periodBudgets ?: emptyList()
                ).getCumulative(),
            expenditureSetup = expenditureSetup(
                options = projectPartnerBudgetOptionsPersistence.getBudgetOptions(partnerId, version) ?: ProjectPartnerBudgetOptions(partnerId),
                budget = getProjectBudget.getBudget(listOf(partner), projectId, version).first(),
                previouslyReported = reportExpenditureCostCategoryPersistence.getCostCategoriesCumulative(submittedReportIds),
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
                previouslyReported = historicalContributions[uuid]?.sumOf { it } ?: BigDecimal.ZERO,
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
            previouslyReported = historicalContributions[uuid]?.sumOf { it } ?: BigDecimal.ZERO,
            currentlyReported = ZERO,
        )
    }

    private fun List<ProjectLumpSum>.toPartnerReportLumpSums(
        partnerId: Long
    ) = map {
        PartnerReportLumpSum(
            lumpSumId = it.programmeLumpSumId,
            period = it.period,
            value = it.lumpSumContributions.firstOrNull { it.partnerId == partnerId }?.amount ?: BigDecimal.ZERO
        )
    }

    private fun getSetOfUnitCostsWithTotalAndNumberOfUnits(budgetEntries: List<BaseBudgetEntry>): Set<PartnerReportUnitCostBase> {
        return budgetEntries.filter {it.unitCostId != null}.groupBy { it.unitCostId }.entries.map { mapOfUnitCostsById -> PartnerReportUnitCostBase(
            unitCostId = mapOfUnitCostsById.key!!,
            totalCost = mapOfUnitCostsById.value.sumOf { it.rowSum!! },
            numberOfUnits = mapOfUnitCostsById.value.sumOf { it.numberOfUnits },
        ) }.toSet()
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
        previouslyReported: BudgetCostsCalculationResultFull,
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
        previouslyReported = previouslyReported,
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
}
