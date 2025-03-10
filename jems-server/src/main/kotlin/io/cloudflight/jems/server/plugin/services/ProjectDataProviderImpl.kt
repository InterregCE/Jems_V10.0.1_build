package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.plugin.contract.models.project.ProjectData
import io.cloudflight.jems.plugin.contract.models.project.ProjectIdentificationData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectLifecycleData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA4.ProjectResultIndicatorOverview
import io.cloudflight.jems.plugin.contract.models.project.sectionB.ProjectDataSectionB
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.PartnerBudgetData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerBudgetOptionsData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerSummaryData
import io.cloudflight.jems.plugin.contract.models.project.sectionD.ProjectDataSectionD
import io.cloudflight.jems.plugin.contract.models.project.sectionE.ProjectDataSectionE
import io.cloudflight.jems.plugin.contract.models.project.versions.ProjectVersionData
import io.cloudflight.jems.plugin.contract.services.ProjectDataProvider
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.ProgrammeDataRepository
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
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingCategoryOverview
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingOverview
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.common.PartnerBudgetPerFundCalculatorService
import io.cloudflight.jems.server.project.service.contracting.fillEndDateWithDuration
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectSearchRequest
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.get_project_result_indicators_overview.ResultOverviewCalculator
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal.ZERO
import java.math.BigDecimal.valueOf
import java.time.ZoneId

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
    private val listResultIndicatorsPersistence: ResultIndicatorPersistence,
    private val programmeDataRepository: ProgrammeDataRepository,
    private val projectUnitCostPersistence: ProjectUnitCostPersistence,
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
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
        val isSpfCall = project.callSettings.callType == CallType.SPF
        val legalStatuses = programmeLegalStatusPersistence.getMax50Statuses()
        val lumpSums = projectLumpSumPersistence.getLumpSums(projectId, version)

        val partners = partnerPersistence.findTop50ByProjectId(projectId, version)
        val partnersSummary = partners.toProjectPartnerSummary()
        val partnerIds = partners.mapTo(HashSet()) { it.id }
        val partnersBudgetOptions = budgetOptionsPersistence.getBudgetOptions(partnerIds, projectId, version)

        val budgetCoFinancingAndContributions: MutableMap<Long, ProjectPartnerCoFinancingAndContribution> =
            mutableMapOf()
        val budgetSPFCoFinancingAndContributions: MutableMap<Long, ProjectPartnerCoFinancingAndContributionSpf> =
            mutableMapOf()

        val partnersData = partners.map { partner ->
            val budgetOptions = partnersBudgetOptions.firstOrNull() { it.partnerId == partner.id }
            val coFinancing = coFinancingPersistence.getCoFinancingAndContributions(partner.id, version).also {
                budgetCoFinancingAndContributions[partner.id] = it
            }.toDataModel()

            val spfCoFinancing =
                if (isSpfCall)
                    coFinancingPersistence.getSpfCoFinancingAndContributions(partner.id, version).also {
                        budgetSPFCoFinancingAndContributions[partner.id] = it
                    }.toDataModel()
                else null
            val spfTotalBudget =
                if (isSpfCall)
                    getBudgetCostsPersistence.getBudgetSpfCostTotal(partner.id, version)
                else valueOf(0, 2)

            val budgetCosts = BudgetCosts(
                staffCosts = getBudgetCostsPersistence.getBudgetStaffCosts(setOf(partner.id), version),
                travelCosts = getBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(setOf(partner.id), version),
                externalCosts = getBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(
                    setOf(partner.id),
                    version
                ),
                equipmentCosts = getBudgetCostsPersistence.getBudgetEquipmentCosts(setOf(partner.id), version),
                infrastructureCosts = getBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(
                    setOf(partner.id),
                    version
                ),
                unitCosts = getBudgetCostsPersistence.getBudgetUnitCosts(setOf(partner.id), version),
                spfCosts = getBudgetCostsPersistence.getBudgetSpfCosts(setOf(partner.id), version)
            ).toDataModel()
            val budgetCalculationResult = getBudgetTotalCosts(budgetOptions, partner.id, version).toDataModel()
            val budget = PartnerBudgetData(
                budgetOptions?.toDataModel(),
                coFinancing,
                budgetCosts,
                budgetCalculationResult.totalCosts,
                budgetCalculationResult,
                spfCoFinancing,
                spfTotalBudget
            )
            val stateAid = partnerPersistence.getPartnerStateAid(partnerId = partner.id, version)

            partner.toDataModel(
                stateAid,
                budget,
                legalStatuses.firstOrNull { it.id == partner.legalStatusId }?.description ?: emptySet()
            )
        }.toSet()

        val sectionA = project.toDataModel(
            tableA3data = getCoFinancingOverview(partnersData, version, isSpfCall),
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

        val projectPeriods = projectPersistence.getProjectPeriods(projectId, version)
        val spfPartnerBudgetPerPeriod = getSpfPartnerBudgetPerPeriod(
            partnerSummary = partnersSummary
                .firstOrNull { isSpfCall && it.active && it.role == ProjectPartnerRole.LEAD_PARTNER },
            projectPeriods = projectPeriods,
            projectId = projectId,
            version = version
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
                            total = partner.budget.projectBudgetCostsCalculationResult.totalCosts - partner.budget.projectPartnerSpfBudgetTotalCost
                        )
                    },
                    spfCoFinancing = emptyList()
                ).toProjectPartnerBudgetPerFundData()
            },
            projectPartnerBudgetPerPeriodData = partnerBudgetPerPeriodCalculator.calculate(
                PartnersAggregatedInfo(
                    partnersSummary, partnersBudgetOptions,
                    projectBudgetPersistence.getBudgetPerPartner(partnerIds, projectId, version),
                    projectBudgetPersistence.getBudgetTotalForPartners(partnerIds, projectId, version)
                ),
                lumpSums = lumpSums,
                projectPeriods = projectPeriods,
                spfPartnerBudgetPerPeriod = spfPartnerBudgetPerPeriod
            ).toProjectBudgetOverviewPerPartnerPerPeriod()
        )

        val sectionE = ProjectDataSectionE(
            projectLumpSums = lumpSums.toDataModel(programmeLumpSumPersistence.getLumpSums(lumpSums.map { it.programmeLumpSumId })),
            projectDefinedUnitCosts = projectUnitCostPersistence.getProjectUnitCostList(projectId, version).toListDataModel(),
        )

        logger.info("Retrieved project data for project id=$projectId via plugin.")

        return ProjectData(
            sectionA, sectionB, sectionC, sectionD, sectionE,
            lifecycleData = ProjectLifecycleData(
                status = project.projectStatus.status.toDataModel(),
                submissionDateStepOne = project.firstSubmissionStep1?.updated,
                firstSubmissionDate = project.firstSubmission?.updated,
                lastResubmissionDate = project.lastResubmission?.updated,
                contractedDate = project.contractedOnDate?.atStartOfDay(ZoneId.systemDefault()),
                assessmentStep1 = project.assessmentStep1?.toDataModel(),
                assessmentStep2 = project.assessmentStep2?.toDataModel()
            ),
            versions = projectVersionPersistence.getAllVersionsByProjectId(projectId).toDataModel(),
            programmeTitle = programmeDataRepository.findById(1)
                .orElseThrow { ResourceNotFoundException("programmeData") }.title ?: "",
            dimensionCodes = contractingMonitoringPersistence.getContractingMonitoring(projectId).dimensionCodes.toContractingDimensionCodeDataList()
        )
    }


    @Transactional(readOnly = true)
    override fun getProjectIdentificationData(projectId: Long): ProjectIdentificationData {
        val project = projectPersistence.getProject(projectId, projectVersionPersistence.getLatestApprovedOrCurrent(projectId))
        val contractMonitoring = contractingMonitoringPersistence.getContractingMonitoring(projectId)
            .fillEndDateWithDuration(resolveDuration = { project.duration })
        val programmeTitle = programmeDataRepository.findById(1)
            .orElseThrow { ResourceNotFoundException("programmeData") }.title ?: ""
        return project.toIdentificationDataModel(
            projectStartDate = contractMonitoring.startDate,
            projectEndDate = contractMonitoring.endDate,
            programmeTitle = programmeTitle,
            projectLifecycleData = ProjectLifecycleData(
                status = project.projectStatus.status.toDataModel(),
                submissionDateStepOne = project.firstSubmissionStep1?.updated,
                firstSubmissionDate = project.firstSubmission?.updated,
                lastResubmissionDate = project.lastResubmission?.updated,
                contractedDate = project.contractedDecision?.updated,
                assessmentStep1 = project.assessmentStep1?.toDataModel(),
                assessmentStep2 = project.assessmentStep2?.toDataModel()
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun getProjectPartnerBudgetOptions(
        partnerId: Long,
        version: String?
    ): ProjectPartnerBudgetOptionsData? = budgetOptionsPersistence.getBudgetOptions(partnerId, version)?.toDataModel()


    @Transactional(readOnly = true)
    override fun getProjectPartnerSummaryData(partnerId: Long): ProjectPartnerSummaryData =
        partnerPersistence.getById(partnerId).toSummaryDataModel()


    @Transactional(readOnly = true)
    override fun getProjectIdsByCallIdIn(callIds: Set<Long>): List<Long> {
        val searchRequest = ProjectSearchRequest(
            calls = callIds,
            id = null,
            acronym = null,
            firstSubmissionFrom = null,
            firstSubmissionTo = null,
            lastSubmissionFrom = null,
            lastSubmissionTo = null,
            objectives = null,
            statuses = null,
            users = null
        )
        return projectPersistence.getProjects(Pageable.unpaged(), searchRequest).content.map { it.id }
    }

    @Transactional(readOnly = true)
    override fun getAllProjectVersionsByProjectIdIn(projectIds: Set<Long>): List<ProjectVersionData> {
        return projectVersionPersistence.getAllVersionsByProjectIdIn(projectIds).toDataModel()
    }

    private fun getSpfPartnerBudgetPerPeriod(
        partnerSummary: ProjectPartnerSummary?,
        projectPeriods: List<ProjectPeriod>,
        projectId: Long,
        version: String?
    ): List<ProjectPartnerBudgetPerPeriod> {

        return if (partnerSummary?.id != null) {
            partnerBudgetPerPeriodCalculator.calculateSpfPartnerBudgetPerPeriod(
                spfBeneficiary = partnerSummary,
                projectPeriods = projectPeriods,
                spfBudgetPerPeriod = projectBudgetPersistence.getSpfBudgetPerPeriod(partnerSummary.id, projectId, version).toMutableList(),
                spfTotalBudget = getBudgetCostsPersistence.getBudgetSpfCostTotal(partnerSummary.id, version)
            )
        } else {
            emptyList()
        }
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

        val spfCosts = getBudgetCostsPersistence.getBudgetSpfCostTotal(partnerId, version)

        return budgetCostsCalculator.calculateCosts(
            budgetOptions,
            unitCostTotal,
            lumpSumsTotal,
            externalCostTotal,
            equipmentCostTotal,
            infrastructureCostTotal,
            travelCostTotal,
            staffCostTotal,
            spfCosts,
        )
    }

    private fun getCoFinancingOverview(
        partners: Set<ProjectPartnerData>,
        version: String?,
        isSpfCall: Boolean
    ): ProjectCoFinancingOverview {
        val partnersByIds = partners.associateBy { it.id!! }
        val funds =
            if (partnersByIds.keys.isNotEmpty()) coFinancingPersistence.getAvailableFunds(partnersByIds.keys.first()) else emptySet()

        val managementCoFinancingOverview = CoFinancingOverviewCalculator.calculateCoFinancingOverview(
            partnerIds = partnersByIds.keys,
            getBudgetTotalCost = { partnerId ->
                val spfTotal = partnersByIds[partnerId]?.budget?.projectPartnerSpfBudgetTotalCost ?: ZERO
                val total = partnersByIds[partnerId]?.budget?.projectBudgetCostsCalculationResult?.totalCosts ?: ZERO
                val managementTotal = total.minus(spfTotal)
                managementTotal
            },
            getCoFinancingAndContributions = { coFinancingPersistence.getCoFinancingAndContributions(it, version) },
            funds = funds,
        )

        val spfCoFinancingCategoryOverview = if (isSpfCall) {
            CoFinancingOverviewCalculator.calculateCoFinancingOverview(
                partnerIds = partnersByIds.keys,
                getBudgetTotalCost = { partnerId ->
                    partnersByIds[partnerId]?.budget?.projectPartnerSpfBudgetTotalCost ?: ZERO
                },
                getCoFinancingAndContributions = { coFinancingPersistence.getSpfCoFinancingAndContributions(it, version) },
                funds = funds,
            )
        } else ProjectCoFinancingCategoryOverview()

        return ProjectCoFinancingOverview(
            projectManagementCoFinancing = managementCoFinancingOverview,
            projectSpfCoFinancing = spfCoFinancingCategoryOverview
        )
    }

    private fun getResultIndicatorOverview(projectId: Long, version: String?): ProjectResultIndicatorOverview {
        return ProjectResultIndicatorOverview(
            indicatorLines = ResultOverviewCalculator.calculateProjectResultOverview(
                projectOutputs = workPackagePersistence.getAllOutputsForProjectIdSortedByNumbers(projectId, version),
                programmeOutputIndicatorsById = listOutputIndicatorsPersistence.getTop250OutputIndicators()
                    .associateBy { it.id },
                programmeResultIndicatorsById = listResultIndicatorsPersistence.getTop50ResultIndicators()
                    .associateBy { it.id },
                projectResultsByIndicatorId = projectResultPersistence.getResultsForProject(projectId, version)
                    .filter { it.programmeResultIndicatorId != null }
                    .groupBy { it.programmeResultIndicatorId }
                    .toMutableMap()
            ).toIndicatorOverviewLines(),
            indicatorLinesWithCodes = ResultOverviewCalculator.calculateProjectResultOverview(
                projectOutputs = workPackagePersistence.getAllOutputsForProjectIdSortedByNumbers(projectId, version),
                programmeOutputIndicatorsById = listOutputIndicatorsPersistence.getTop250OutputIndicators()
                    .associateBy { it.id },
                programmeResultIndicatorsById = listResultIndicatorsPersistence.getTop50ResultIndicators()
                    .associateBy { it.id },
                projectResultsByIndicatorId = projectResultPersistence.getResultsForProject(projectId, version)
                    .filter { it.programmeResultIndicatorId != null }
                    .groupBy { it.programmeResultIndicatorId }
                    .toMutableMap()
            ).toIndicatorOverviewLinesWithCodes()
        )
    }
}
