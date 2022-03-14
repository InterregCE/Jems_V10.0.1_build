package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.ProjectData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectLifecycleData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA4.ProjectResultIndicatorOverview
import io.cloudflight.jems.plugin.contract.models.project.sectionB.ProjectDataSectionB
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.PartnerBudgetData
import io.cloudflight.jems.plugin.contract.models.project.sectionD.ProjectDataSectionD
import io.cloudflight.jems.plugin.contract.models.project.versions.ProjectVersionData
import io.cloudflight.jems.plugin.contract.services.ProjectDataProvider
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.legalstatus.ProgrammeLegalStatusPersistence
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.associatedorganization.AssociatedOrganizationPersistence
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.PartnerBudgetPerPeriodCalculator
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.budget.model.PartnersAggregatedInfo
import io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview.CoFinancingOverviewCalculator
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingOverview
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.common.PartnerBudgetPerFundCalculatorService
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.get_project_result_indicators_overview.ResultOverviewCalculator
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal.ZERO

@Service
class ProjectDataProviderImpl(
    private val callPersistence: CallPersistence,
    private val projectPersistence: ProjectPersistence,
    private val projectVersionPersistence: ProjectVersionPersistence,
    private val programmeLumpSumPersistence: ProgrammeLumpSumPersistence,
    private val projectDescriptionPersistence: ProjectDescriptionPersistence,
    private val projectBudgetPersistence: ProjectBudgetPersistence,
    private val workPackagePersistence: WorkPackagePersistence,
    private val resultPersistence: ProjectResultPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val associatedOrganizationPersistence: AssociatedOrganizationPersistence,
    private val budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val coFinancingPersistence: ProjectPartnerCoFinancingPersistence,
    private val getBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val budgetCostsCalculator: BudgetCostsCalculatorService,
    private val partnerBudgetPerFundCalculator: PartnerBudgetPerFundCalculatorService,
    private val partnerBudgetPerPeriodCalculator: PartnerBudgetPerPeriodCalculator,
    private val projectLumpSumPersistence: ProjectLumpSumPersistence,
    private val programmeLegalStatusPersistence: ProgrammeLegalStatusPersistence,
    private val projectResultPersistence: ProjectResultPersistence,
    private val listOutputIndicatorsPersistence: OutputIndicatorPersistence,
    private val listResultIndicatorsPersistence: ResultIndicatorPersistence
) : ProjectDataProvider {

    companion object {
        private val logger = LoggerFactory.getLogger(ProjectDataProviderImpl::class.java)
    }

    @Transactional(readOnly = true)
    override fun getAllProjectVersions(): List<ProjectVersionData> =
        projectVersionPersistence.getAllVersions().toDataModel()

    @Transactional(readOnly = true)
    override fun getProjectDataForProjectId(projectId: Long, version: String?): ProjectData {
        val project = projectPersistence.getProject(projectId, version)
        val legalStatuses = programmeLegalStatusPersistence.getMax20Statuses()
        val lumpSums = projectLumpSumPersistence.getLumpSums(projectId, version)

        val partners = partnerPersistence.findTop30ByProjectId(projectId, version)
        val partnersSummary = partners.toProjectPartnerSummary()
        val partnerIds = partners.mapTo(HashSet()) { it.id }
        val partnersBudgetOptions = budgetOptionsPersistence.getBudgetOptions(partnerIds, projectId, version)

        val budgetCoFinancingAndContributions: MutableMap<Long, ProjectPartnerCoFinancingAndContribution> =
            mutableMapOf()

        val partnersData = partners.map { partner ->
            val budgetOptions = partnersBudgetOptions.firstOrNull() { it.partnerId == partner.id }
            val coFinancing = coFinancingPersistence.getCoFinancingAndContributions(partner.id, version).also {
                budgetCoFinancingAndContributions[partner.id] = it
            }.toDataModel()

            val budgetCosts = BudgetCosts(
                staffCosts = getBudgetCostsPersistence.getBudgetStaffCosts(partner.id, version),
                travelCosts = getBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partner.id, version),
                externalCosts = getBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(
                    partner.id,
                    version
                ),
                equipmentCosts = getBudgetCostsPersistence.getBudgetEquipmentCosts(partner.id, version),
                infrastructureCosts = getBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(
                    partner.id,
                    version
                ),
                unitCosts = getBudgetCostsPersistence.getBudgetUnitCosts(partner.id, version),
                spfCosts = getBudgetCostsPersistence.getBudgetSpfCosts(partner.id, version)
            ).toDataModel()
            val budgetCalculationResult = getBudgetTotalCosts(budgetOptions, partner.id, version).toDataModel()
            val budget = PartnerBudgetData(
                budgetOptions?.toDataModel(),
                coFinancing,
                budgetCosts,
                budgetCalculationResult.totalCosts,
                budgetCalculationResult
            )
            val stateAid = partnerPersistence.getPartnerStateAid(partnerId = partner.id, version)

            partner.toDataModel(
                stateAid,
                budget,
                legalStatuses.firstOrNull { it.id == partner.legalStatusId }?.description ?: emptySet()
            )
        }.toSet()

        val sectionA = project.toDataModel(
            tableA3data = getCoFinancingOverview(partnersData, version),
            tableA4data = getResultIndicatorOverview(projectId, version)
        )

        val sectionB =
            ProjectDataSectionB(
                partnersData,
                associatedOrganizationPersistence.findAllByProjectId(projectId, version).toDataModel()
            )

        val sectionC = projectDescriptionPersistence.getProjectDescription(projectId, version).toDataModel(
            workPackages = workPackagePersistence.getWorkPackagesWithAllDataByProjectId(projectId, version),
            results = resultPersistence.getResultsForProject(projectId, version)
        )
        val sectionD = ProjectDataSectionD(
            projectPartnerBudgetPerFundData = partnersSummary.let { partnerSummaries ->
                partnerBudgetPerFundCalculator.calculate(
                    partners = partnerSummaries,
                    projectFunds = callPersistence.getCallByProjectId(projectId).funds.map { it.programmeFund },
                    coFinancing = partnersData.map { partner ->
                        PartnerBudgetCoFinancing(
                            partner = partnerSummaries.first { it.id == partner.id },
                            budgetCoFinancingAndContributions[partner.id],
                            total = partner.budget.projectBudgetCostsCalculationResult.totalCosts
                        )
                    },
                    spfCoFinancing = null
                ).toProjectPartnerBudgetPerFundData()
            },
            projectPartnerBudgetPerPeriodData = partnerBudgetPerPeriodCalculator.calculate(
                PartnersAggregatedInfo(
                    partnersSummary, partnersBudgetOptions,
                    projectBudgetPersistence.getBudgetPerPartner(partnerIds, projectId, version),
                    projectBudgetPersistence.getBudgetTotalForPartners(partnerIds, projectId, version)
                ),
                lumpSums = lumpSums,
                projectPeriods = projectPersistence.getProjectPeriods(projectId, version),
            ).toProjectBudgetOverviewPerPartnerPerPeriod()
        )

        val sectionE = with(lumpSums) {
            this.toDataModel(programmeLumpSumPersistence.getLumpSums(this.map { it.programmeLumpSumId }))
        }

        logger.info("Retrieved project data for project id=$projectId via plugin.")

        return ProjectData(
            sectionA, sectionB, sectionC, sectionD, sectionE,
            lifecycleData = ProjectLifecycleData(
                status = project.projectStatus.status.toDataModel(),
                submissionDateStepOne = project.firstSubmissionStep1?.updated,
                firstSubmissionDate = project.firstSubmission?.updated,
                lastResubmissionDate = project.lastResubmission?.updated,
                contractedDate = project.contractedDecision?.updated,
                assessmentStep1 = project.assessmentStep1?.toDataModel(),
                assessmentStep2 = project.assessmentStep2?.toDataModel()
            ),
            versions = projectVersionPersistence.getAllVersionsByProjectId(projectId).toDataModel()
        )
    }

    private fun getBudgetTotalCosts(
        budgetOptions: ProjectPartnerBudgetOptions?,
        partnerId: Long,
        version: String?
    ): BudgetCostsCalculationResult {
        val unitCostTotal = getBudgetCostsPersistence.getBudgetUnitCostTotal(partnerId, version)
        val lumpSumsTotal = getBudgetCostsPersistence.getBudgetLumpSumsCostTotal(partnerId, version)
        val equipmentCostTotal = getBudgetCostsPersistence.getBudgetEquipmentCostTotal(partnerId, version)
        val externalCostTotal =
            getBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCostTotal(partnerId, version)
        val infrastructureCostTotal =
            getBudgetCostsPersistence.getBudgetInfrastructureAndWorksCostTotal(partnerId, version)

        val travelCostTotal = if (budgetOptions?.travelAndAccommodationOnStaffCostsFlatRate == null)
            getBudgetCostsPersistence.getBudgetTravelAndAccommodationCostTotal(partnerId, version)
        else
            ZERO

        val staffCostTotal = if (budgetOptions?.staffCostsFlatRate == null)
            getBudgetCostsPersistence.getBudgetStaffCostTotal(partnerId, version)
        else
            ZERO

        return budgetCostsCalculator.calculateCosts(
            budgetOptions,
            unitCostTotal,
            lumpSumsTotal,
            externalCostTotal,
            equipmentCostTotal,
            infrastructureCostTotal,
            travelCostTotal,
            staffCostTotal
        )
    }

    private fun getCoFinancingOverview(
        partners: Set<ProjectPartnerData>,
        version: String?
    ): ProjectCoFinancingOverview {
        val partnersByIds = partners.associateBy { it.id!! }
        val funds =
            if (partnersByIds.keys.isNotEmpty()) coFinancingPersistence.getAvailableFunds(partnersByIds.keys.first()) else emptySet()

        return CoFinancingOverviewCalculator.calculateCoFinancingOverview(
            partnerIds = partnersByIds.keys,
            getBudgetTotalCost = { partnerId ->
                partnersByIds[partnerId]?.budget?.projectBudgetCostsCalculationResult?.totalCosts ?: ZERO
            },
            getCoFinancingAndContributions = { coFinancingPersistence.getCoFinancingAndContributions(it, version) },
            funds = funds,
        )
    }

    private fun getResultIndicatorOverview(projectId: Long, version: String?): ProjectResultIndicatorOverview {
        return ProjectResultIndicatorOverview(
            indicatorLines = ResultOverviewCalculator.calculateProjectResultOverview(
                projectOutputs = workPackagePersistence.getAllOutputsForProjectIdSortedByNumbers(projectId, version),
                programmeOutputIndicatorsById = listOutputIndicatorsPersistence.getTop50OutputIndicators()
                    .associateBy { it.id },
                programmeResultIndicatorsById = listResultIndicatorsPersistence.getTop50ResultIndicators()
                    .associateBy { it.id },
                projectResultsByIndicatorId = projectResultPersistence.getResultsForProject(projectId, version)
                    .filter { it.programmeResultIndicatorId != null }
                    .groupBy { it.programmeResultIndicatorId }
                    .toMutableMap()
            ).toIndicatorOverviewLines()
        )
    }
}
