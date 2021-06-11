package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecovery
import io.cloudflight.jems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.status.ProjectEligibilityAssessmentResult
import io.cloudflight.jems.api.project.dto.status.ProjectQualityAssessmentResult
import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.models.programme.strategy.ProgrammeStrategyData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.ProjectDataSectionA
import io.cloudflight.jems.plugin.contract.models.project.sectionB.ProjectDataSectionB
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerVatRecoveryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetCostData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.PartnerBudgetData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerBudgetOptionsData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingAndContributionData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.ProjectDataSectionC
import io.cloudflight.jems.plugin.contract.models.project.sectionC.longTermPlans.ProjectLongTermPlansData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectCooperationCriteriaData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectHorizontalPrinciplesData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectHorizontalPrinciplesEffectData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.management.ProjectManagementData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.overallObjective.ProjectOverallObjectiveData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.partnership.ProjectPartnershipData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceBenefitData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceStrategyData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectRelevanceSynergyData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectTargetGroupData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.ProjectWorkPackageData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityDeliverableData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityTranslatedValueData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageInvestmentAddressData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageInvestmentData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageOutputData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageOutputTranslatedValueData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.ProjectDataSectionE
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectCooperationCriteria
import io.cloudflight.jems.server.project.service.model.ProjectDecision
import io.cloudflight.jems.server.project.service.model.ProjectDescription
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectLongTermPlans
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectOverallObjective
import io.cloudflight.jems.server.project.service.model.ProjectPartnership
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectRelevance
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceStrategy
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceSynergy
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs.GetBudgetCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCostInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class ProjectDataProviderImplTest : UnitTest() {

    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistence
    @RelaxedMockK
    lateinit var projectDescriptionPersistence: ProjectDescriptionPersistence
    @RelaxedMockK
    lateinit var workPackagePersistence: WorkPackagePersistence
    @RelaxedMockK
    lateinit var resultPersistence: ProjectResultPersistence
    @RelaxedMockK
    lateinit var partnerPersistence: PartnerPersistence
    @RelaxedMockK
    lateinit var associatedOrganizationService: ProjectAssociatedOrganizationService
    @RelaxedMockK
    lateinit var budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence
    @RelaxedMockK
    lateinit var coFinancingPersistence: ProjectPartnerCoFinancingPersistence
    @RelaxedMockK
    lateinit var getBudgetCosts: GetBudgetCostsInteractor
    @RelaxedMockK
    lateinit var getBudgetTotalCost: GetBudgetTotalCostInteractor
    @RelaxedMockK
    lateinit var projectLumpSumPersistence: ProjectLumpSumPersistence
    @RelaxedMockK
    lateinit var programmeLumpSumPersistence: ProgrammeLumpSumPersistence

    @InjectMockKs
    lateinit var projectDataProvider: ProjectDataProviderImpl

    companion object {
        private val startDate = ZonedDateTime.now().minusDays(2)
        private val endDate = ZonedDateTime.now().plusDays(5)

        private val user = UserSummary(3L, "email", "name", "surname", UserRoleSummary(4L, "role"))
        private val projectStatus = ProjectStatus(5L, ApplicationStatus.APPROVED, user, updated = startDate)
        private val callSettings = ProjectCallSettings(
            callId = 2L,
            callName = "call",
            startDate = startDate,
            endDate = endDate,
            lengthOfPeriod = 2,
            endDateStep1 = endDate,
            flatRates = emptySet(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
            isAdditionalFundAllowed = false
        )
        private val project = Project(
            id = 1L,
            callSettings = callSettings,
            acronym = "acronym",
            applicant = user,
            duration = 12,
            programmePriority = null,
            specificObjective = null,
            projectStatus = projectStatus,
            step2Active = false,
            periods = listOf(ProjectPeriod(1, 1, 1), ProjectPeriod(2, 2, 2)),
            firstStepDecision = ProjectDecision(
                OutputProjectQualityAssessment(
                    ProjectQualityAssessmentResult.NOT_RECOMMENDED,
                    updated = startDate
                ),
                OutputProjectEligibilityAssessment(
                    ProjectEligibilityAssessmentResult.FAILED,
                    updated = startDate
                ),
                projectStatus
            ),
            title = setOf(InputTranslation(SystemLanguage.EN, "title"))
        )
        private val projectDescription = ProjectDescription(
            projectOverallObjective = ProjectOverallObjective(
                overallObjective = setOf(InputTranslation(SystemLanguage.EN, "overallObjective"))
            ),
            projectRelevance = ProjectRelevance(
                territorialChallenge = setOf(InputTranslation(SystemLanguage.EN, "territorialChallenge")),
                commonChallenge = setOf(InputTranslation(SystemLanguage.EN, "commonChallenge")),
                transnationalCooperation = setOf(InputTranslation(SystemLanguage.EN, "transnationalCooperation")),
                projectBenefits = listOf(ProjectRelevanceBenefit(
                    group = ProjectTargetGroup.LocalPublicAuthority,
                    specification = setOf(InputTranslation(SystemLanguage.EN, "specification")))
                ),
                projectStrategies = listOf(ProjectRelevanceStrategy(
                    strategy = ProgrammeStrategy.AtlanticStrategy,
                    specification = setOf(InputTranslation(SystemLanguage.EN, "specification")))
                ),
                projectSynergies = listOf(ProjectRelevanceSynergy(
                    synergy = setOf(InputTranslation(SystemLanguage.EN, "synergy")),
                    specification = setOf(InputTranslation(SystemLanguage.EN, "specification")))
                ),
                availableKnowledge = setOf(InputTranslation(SystemLanguage.EN, "availableKnowledge"))
            ),
            projectPartnership = ProjectPartnership(
                partnership = setOf(InputTranslation(SystemLanguage.EN, "partnership"))
            ),
            projectManagement = ProjectManagement(
                projectCoordination = setOf(InputTranslation(SystemLanguage.EN, "projectCoordination")),
                projectQualityAssurance = setOf(InputTranslation(SystemLanguage.EN, "projectQualityAssurance")),
                projectCommunication = setOf(InputTranslation(SystemLanguage.EN, "projectCommunication")),
                projectFinancialManagement = setOf(InputTranslation(SystemLanguage.EN, "projectFinancialManagement")),
                projectCooperationCriteria = ProjectCooperationCriteria(
                    projectJointDevelopment = true,
                    projectJointFinancing = true,
                    projectJointImplementation = true,
                    projectJointStaffing = true
                ),
                projectJointDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointDevelopmentDescription")),
                projectJointImplementationDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointImplementationDescription")),
                projectJointStaffingDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointStaffingDescription")),
                projectJointFinancingDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointFinancingDescription")),
                projectHorizontalPrinciples = ProjectHorizontalPrinciples(
                    sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
                    equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.Neutral,
                    sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects
                ),
                sustainableDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "sustainableDevelopmentDescription")),
                equalOpportunitiesDescription = setOf(InputTranslation(SystemLanguage.EN, "equalOpportunitiesDescription")),
                sexualEqualityDescription = setOf(InputTranslation(SystemLanguage.EN, "sexualEqualityDescription"))
            ),
            projectLongTermPlans = ProjectLongTermPlans(
                projectOwnership = setOf(InputTranslation(SystemLanguage.EN, "projectOwnership")),
                projectDurability = setOf(InputTranslation(SystemLanguage.EN, "projectDurability")),
                projectTransferability = setOf(InputTranslation(SystemLanguage.EN, "projectTransferability"))
            )
        )
        private val projectPartner = OutputProjectPartnerDetail(
            id = 2L,
            abbreviation = "partner",
            role = ProjectPartnerRole.LEAD_PARTNER,
            nameInOriginalLanguage = "test",
            nameInEnglish = "test",
            partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
            vat = "test vat",
            vatRecovery = ProjectPartnerVatRecovery.Yes,
            legalStatusId = 3L
        )
        private val partnerBudgetOptions = ProjectPartnerBudgetOptions(
            partnerId = projectPartner.id!!
        )
        private val partnerCoFinancing = ProjectPartnerCoFinancingAndContribution(
            finances = emptyList(),
            partnerContributions = emptyList(),
            partnerAbbreviation = projectPartner.abbreviation
        )
        private val budgetCosts = BudgetCosts(
            staffCosts = emptyList(),
            travelCosts = emptyList(),
            externalCosts = emptyList(),
            equipmentCosts = emptyList(),
            infrastructureCosts = emptyList(),
            unitCosts = emptyList()
        )
        private val investment = WorkPackageInvestment(
            id = 2L,
            investmentNumber = 3,
            title = setOf(InputTranslation(SystemLanguage.EN, "title")),
            justificationExplanation = setOf(InputTranslation(SystemLanguage.EN, "justificationExplanation")),
            justificationTransactionalRelevance = setOf(InputTranslation(SystemLanguage.EN, "justificationTransactionalRelevance")),
            justificationBenefits = setOf(InputTranslation(SystemLanguage.EN, "justificationBenefits")),
            justificationPilot = setOf(InputTranslation(SystemLanguage.EN, "justificationPilot")),
            address = Address("country", "reg2", "reg3", "str", "nr", "code", "city"),
            risk = setOf(InputTranslation(SystemLanguage.EN, "risk")),
            documentation = setOf(InputTranslation(SystemLanguage.EN, "documentation")),
            ownershipSiteLocation = setOf(InputTranslation(SystemLanguage.EN, "ownershipSiteLocation")),
            ownershipRetain = setOf(InputTranslation(SystemLanguage.EN, "ownershipRetain")),
            ownershipMaintenance = setOf(InputTranslation(SystemLanguage.EN, "ownershipMaintenance"))
        )
        private val activity = WorkPackageActivity(
            workPackageId = 1L,
            activityNumber = 2,
            translatedValues = setOf(WorkPackageActivityTranslatedValue(SystemLanguage.EN, "title", "description")),
            startPeriod = 3,
            endPeriod = 4,
            deliverables = listOf(WorkPackageActivityDeliverable())
        )
        private val workPackageOutput = WorkPackageOutput(
            workPackageId = 1L,
            outputNumber = 0,
            programmeOutputIndicatorId = null,
            programmeOutputIndicatorIdentifier = "id",
            targetValue = BigDecimal.TEN,
            periodNumber = 1,
            translatedValues = setOf(WorkPackageOutputTranslatedValue(SystemLanguage.EN, "title", "description"))
        )
        private val workPackage = ProjectWorkPackageFull(
            id = 1L,
            workPackageNumber = 1,
            translatedValues = setOf(ProjectWorkPackageTranslatedValue(SystemLanguage.EN, "name", "objective", "audience")),
            activities = listOf(activity),
            outputs = listOf(workPackageOutput),
            investments = listOf(investment)
        )
    }

    @Test
    fun `project data provider get for project Id`() {
        val id = project.id!!
        val totalCost = BigDecimal.TEN
        every { projectPersistence.getProject(id) } returns project
        every { projectDescriptionPersistence.getProjectDescription(id) } returns projectDescription
        every { partnerPersistence.findAllByProjectId(id) } returns listOf(projectPartner)
        every { budgetOptionsPersistence.getBudgetOptions(projectPartner.id!!) } returns partnerBudgetOptions
        every { coFinancingPersistence.getCoFinancingAndContributions(projectPartner.id!!, null) } returns partnerCoFinancing
        every { getBudgetCosts.getBudgetCosts(projectPartner.id!!) } returns budgetCosts
        every { getBudgetTotalCost.getBudgetTotalCost(projectPartner.id!!) } returns totalCost
        every { workPackagePersistence.getWorkPackagesWithAllDataByProjectId(id) } returns listOf(workPackage)

        // test getByProjectId and its mappings..
        val projectData = projectDataProvider.getProjectDataForProjectId(id)

        assertThat(projectData.sectionA).isEqualTo(
            ProjectDataSectionA(
                title = setOf(InputTranslationData(SystemLanguageData.EN, "title")),
                intro = emptySet(),
                acronym = project.acronym,
                duration = project.duration,
                specificObjective = project.specificObjective?.toDataModel(),
                programmePriority = project.programmePriority?.toDataModel()
            )
        )
        assertThat(projectData.sectionB).isEqualTo(
            ProjectDataSectionB(
                partners = setOf(ProjectPartnerData(
                    id = projectPartner.id,
                    abbreviation = projectPartner.abbreviation,
                    role = ProjectPartnerRoleData.valueOf(projectPartner.role.name),
                    nameInOriginalLanguage = projectPartner.nameInOriginalLanguage,
                    nameInEnglish = projectPartner.nameInEnglish,
                    partnerType = ProjectTargetGroupData.valueOf(projectPartner.partnerType!!.name),
                    vat = projectPartner.vat,
                    vatRecovery = ProjectPartnerVatRecoveryData.valueOf(projectPartner.vatRecovery!!.name),
                    legalStatusId = projectPartner.legalStatusId,
                    budget = PartnerBudgetData(
                        projectPartnerOptions = ProjectPartnerBudgetOptionsData(
                            partnerId = projectPartner.id!!
                        ),
                        projectPartnerCoFinancing = ProjectPartnerCoFinancingAndContributionData(
                            finances = emptySet(),
                            partnerContributions = emptySet(),
                            partnerAbbreviation = projectPartner.abbreviation
                        ),
                        projectPartnerBudgetCosts = BudgetCostData(
                            staffCosts = emptyList(),
                            travelCosts = emptyList(),
                            externalCosts = emptyList(),
                            equipmentCosts = emptyList(),
                            infrastructureCosts = emptyList(),
                            unitCosts  = emptyList()
                        ),
                        projectPartnerBudgetTotalCost = totalCost
                    )
                )),
                associatedOrganisations = emptySet()
            )
        )
        assertThat(projectData.sectionC).isEqualTo(
            ProjectDataSectionC(
                projectOverallObjective = ProjectOverallObjectiveData(
                    overallObjective = setOf(InputTranslationData(SystemLanguageData.EN, "overallObjective"))
                ),
                projectRelevance = ProjectRelevanceData(
                    territorialChallenge = setOf(InputTranslationData(SystemLanguageData.EN, "territorialChallenge")),
                    commonChallenge = setOf(InputTranslationData(SystemLanguageData.EN, "commonChallenge")),
                    transnationalCooperation = setOf(InputTranslationData(SystemLanguageData.EN, "transnationalCooperation")),
                    projectBenefits = listOf(ProjectRelevanceBenefitData(
                        group = ProjectTargetGroupData.LocalPublicAuthority,
                        specification = setOf(InputTranslationData(SystemLanguageData.EN, "specification"))
                    )),
                    projectStrategies = listOf(ProjectRelevanceStrategyData(
                        strategy = ProgrammeStrategyData.AtlanticStrategy,
                        specification = setOf(InputTranslationData(SystemLanguageData.EN, "specification"))
                    )),
                    projectSynergies = listOf(ProjectRelevanceSynergyData(
                        synergy = setOf(InputTranslationData(SystemLanguageData.EN, "synergy")),
                        specification = setOf(InputTranslationData(SystemLanguageData.EN, "specification"))
                    )),
                    availableKnowledge = setOf(InputTranslationData(SystemLanguageData.EN, "availableKnowledge"))
                ),
                projectPartnership = ProjectPartnershipData(
                    partnership = setOf(InputTranslationData(SystemLanguageData.EN, "partnership"))
                ),
                projectWorkPackages = listOf(ProjectWorkPackageData(
                    id = workPackage.id,
                    workPackageNumber = workPackage.workPackageNumber,
                    name = setOf(InputTranslationData(SystemLanguageData.EN, "name")),
                    specificObjective = setOf(InputTranslationData(SystemLanguageData.EN, "objective")),
                    objectiveAndAudience = setOf(InputTranslationData(SystemLanguageData.EN, "audience")),
                    activities = listOf(WorkPackageActivityData(
                        activityNumber = activity.activityNumber,
                        translatedValues = setOf(WorkPackageActivityTranslatedValueData(SystemLanguageData.EN, "title", "description")),
                        startPeriod = activity.startPeriod,
                        endPeriod = activity.endPeriod,
                        deliverables = listOf(WorkPackageActivityDeliverableData())
                    )),
                    outputs = listOf(WorkPackageOutputData(
                        outputNumber = workPackageOutput.outputNumber,
                        programmeOutputIndicatorId = workPackageOutput.programmeOutputIndicatorId,
                        programmeOutputIndicatorIdentifier = workPackageOutput.programmeOutputIndicatorIdentifier,
                        targetValue = workPackageOutput.targetValue,
                        periodNumber = workPackageOutput.periodNumber,
                        translatedValues = setOf(WorkPackageOutputTranslatedValueData(SystemLanguageData.EN, "title", "description"))
                    )),
                    investments = listOf(WorkPackageInvestmentData(
                        id = investment.id,
                        investmentNumber = investment.investmentNumber,
                        title = setOf(InputTranslationData(SystemLanguageData.EN, "title")),
                        justificationExplanation = setOf(InputTranslationData(SystemLanguageData.EN, "justificationExplanation")),
                        justificationTransactionalRelevance = setOf(InputTranslationData(SystemLanguageData.EN, "justificationTransactionalRelevance")),
                        justificationBenefits = setOf(InputTranslationData(SystemLanguageData.EN, "justificationBenefits")),
                        justificationPilot = setOf(InputTranslationData(SystemLanguageData.EN, "justificationPilot")),
                        address = WorkPackageInvestmentAddressData("country", "reg2", "reg3", "str", "nr", "code", "city"),
                        risk = setOf(InputTranslationData(SystemLanguageData.EN, "risk")),
                        documentation = setOf(InputTranslationData(SystemLanguageData.EN, "documentation")),
                        ownershipSiteLocation = setOf(InputTranslationData(SystemLanguageData.EN, "ownershipSiteLocation")),
                        ownershipRetain = setOf(InputTranslationData(SystemLanguageData.EN, "ownershipRetain")),
                        ownershipMaintenance = setOf(InputTranslationData(SystemLanguageData.EN, "ownershipMaintenance"))
                    ))
                )),
                projectResults = emptyList(),
                projectManagement = ProjectManagementData(
                    projectCoordination = setOf(InputTranslationData(SystemLanguageData.EN, "projectCoordination")),
                    projectQualityAssurance = setOf(InputTranslationData(SystemLanguageData.EN, "projectQualityAssurance")),
                    projectCommunication = setOf(InputTranslationData(SystemLanguageData.EN, "projectCommunication")),
                    projectFinancialManagement = setOf(InputTranslationData(SystemLanguageData.EN, "projectFinancialManagement")),
                    projectCooperationCriteria = ProjectCooperationCriteriaData(
                        projectJointStaffing = true,
                        projectJointImplementation = true,
                        projectJointFinancing = true,
                        projectJointDevelopment = true
                    ),
                    projectJointDevelopmentDescription = setOf(InputTranslationData(SystemLanguageData.EN, "projectJointDevelopmentDescription")),
                    projectJointImplementationDescription = setOf(InputTranslationData(SystemLanguageData.EN, "projectJointImplementationDescription")),
                    projectJointStaffingDescription = setOf(InputTranslationData(SystemLanguageData.EN, "projectJointStaffingDescription")),
                    projectJointFinancingDescription = setOf(InputTranslationData(SystemLanguageData.EN, "projectJointFinancingDescription")),
                    projectHorizontalPrinciples = ProjectHorizontalPrinciplesData(
                        sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffectData.PositiveEffects,
                        equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffectData.Neutral,
                        sexualEqualityEffect = ProjectHorizontalPrinciplesEffectData.NegativeEffects
                    ),
                    sustainableDevelopmentDescription = setOf(InputTranslationData(SystemLanguageData.EN, "sustainableDevelopmentDescription")),
                    equalOpportunitiesDescription = setOf(InputTranslationData(SystemLanguageData.EN, "equalOpportunitiesDescription")),
                    sexualEqualityDescription = setOf(InputTranslationData(SystemLanguageData.EN, "sexualEqualityDescription"))
                ),
                projectLongTermPlans = ProjectLongTermPlansData(
                    projectOwnership = setOf(InputTranslationData(SystemLanguageData.EN, "projectOwnership")),
                    projectDurability = setOf(InputTranslationData(SystemLanguageData.EN, "projectDurability")),
                    projectTransferability = setOf(InputTranslationData(SystemLanguageData.EN, "projectTransferability"))
                )
            )
        )
        assertThat(projectData.sectionE).isEqualTo(
            ProjectDataSectionE(
                projectLumpSums = emptyList()
            )
        )
    }

    @Test
    fun `project data provider get fail for unknown project Id`() {
        val id = 1L
        every { projectPersistence.getProject(id) } throws ResourceNotFoundException("project")

        assertThrows<ResourceNotFoundException> { projectDataProvider.getProjectDataForProjectId(id) }
    }
}
