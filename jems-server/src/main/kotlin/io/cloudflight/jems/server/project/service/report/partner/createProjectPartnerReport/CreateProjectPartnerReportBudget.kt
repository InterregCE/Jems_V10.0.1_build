package io.cloudflight.jems.server.project.service.report.partner.createProjectPartnerReport

import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.BaseBudgetEntry
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportBudget
import io.cloudflight.jems.server.project.service.report.model.create.PartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class CreateProjectPartnerReportBudget(
    private val reportPersistence: ProjectReportPersistence,
    private val reportContributionPersistence: ProjectReportContributionPersistence,
    private val lumpSumPersistence: ProjectLumpSumPersistence,
    private val partnerBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val getPartnerBudgetPerPeriod: GetPartnerBudgetPerPeriodInteractor,
    private val projectPartnerBudgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence,
) {

    @Transactional
    fun retrieveBudgetDataFor(
        projectId: Long,
        partnerId: Long,
        version: String?,
        partnerContributions: Collection<ProjectPartnerContribution>,
    ) = PartnerReportBudget(
        contributions = generateContributionsFromPreviousReports(
            partnerId = partnerId,
            partnerContributionsSorted = partnerContributions.sortedWith(compareBy({ it.isNotPartner() }, { it.id })),
        ),
        lumpSums = lumpSumPersistence.getLumpSums(projectId, version = version).toPartnerReportLumpSums(partnerId = partnerId),
        unitCostIds = partnerBudgetCostsPersistence.getBudgetStaffCosts(partnerId, version).extractUnitCostIds()
            union partnerBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partnerId, version).extractUnitCostIds()
            union partnerBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partnerId, version).extractUnitCostIds()
            union partnerBudgetCostsPersistence.getBudgetEquipmentCosts(partnerId, version).extractUnitCostIds()
            union partnerBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partnerId, version).extractUnitCostIds()
            union partnerBudgetCostsPersistence.getBudgetUnitCosts(partnerId, version).extractUnitCostIds(),
        budgetPerPeriod = getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId = projectId, version)
            .partnersBudgetPerPeriod.firstOrNull { it.partner.id == partnerId }
            ?.periodBudgets?.map { Pair(it.periodNumber, it.totalBudgetPerPeriod) }
            ?: emptyList(),
        budgetOptions = projectPartnerBudgetOptionsPersistence.getBudgetOptions(partnerId, version),
    )

    private fun generateContributionsFromPreviousReports(
        partnerId: Long,
        partnerContributionsSorted: List<ProjectPartnerContribution>,
    ): List<CreateProjectPartnerReportContribution> {
        val submittedReportIds = reportPersistence.listSubmittedPartnerReports(partnerId = partnerId).mapTo(HashSet()) { it.id }

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
                amount = it.amount ?: BigDecimal.ZERO,
                previouslyReported = historicalContributions[uuid]?.sumOf { it } ?: BigDecimal.ZERO,
                currentlyReported = BigDecimal.ZERO,
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
            amount = BigDecimal.ZERO,
            previouslyReported = historicalContributions[uuid]?.sumOf { it } ?: BigDecimal.ZERO,
            currentlyReported = BigDecimal.ZERO,
        )
    }

    private fun List<BaseBudgetEntry>.extractUnitCostIds(): Set<Long> = this.mapNotNullTo(HashSet()) { it.unitCostId }

    private fun List<ProjectLumpSum>.toPartnerReportLumpSums(partnerId: Long) = map {
        PartnerReportLumpSum(
            lumpSumId = it.programmeLumpSumId,
            period = it.period,
            value = it.lumpSumContributions.firstOrNull { it.partnerId == partnerId }?.amount ?: BigDecimal.ZERO,
        )
    }
}
