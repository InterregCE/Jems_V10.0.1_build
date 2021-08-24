package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectContactTypeDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationAddress
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerContactDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.models.programme.lumpsum.ProgrammeLumpSumData
import io.cloudflight.jems.plugin.contract.models.programme.lumpsum.ProgrammeLumpSumPhaseData
import io.cloudflight.jems.plugin.contract.models.programme.strategy.ProgrammeStrategyData
import io.cloudflight.jems.plugin.contract.models.programme.unitcost.BudgetCategoryData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ApplicationStatusData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectLifecycleData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.ProjectDataSectionA
import io.cloudflight.jems.plugin.contract.models.project.sectionB.ProjectDataSectionB
import io.cloudflight.jems.plugin.contract.models.project.sectionB.associatedOrganisation.ProjectAssociatedOrganizationAddressData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.associatedOrganisation.ProjectAssociatedOrganizationData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.NaceGroupLevelData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.PartnerSubTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectContactTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerAddressData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerAddressTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerContactData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerEssentialData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerMotivationData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerStateAidData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerVatRecoveryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetCostData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetPeriodData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.BudgetStaffCostEntryData
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
import io.cloudflight.jems.plugin.contract.models.project.sectionC.results.ProjectResultData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.ProjectWorkPackageData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageActivityDeliverableData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageInvestmentAddressData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageInvestmentData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.workpackage.WorkPackageOutputData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.ProjectDataSectionE
import io.cloudflight.jems.plugin.contract.models.project.sectionE.lumpsum.ProjectLumpSumData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.lumpsum.ProjectPartnerLumpSumData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.project.controller.workpackage.extractField
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectCooperationCriteria
import io.cloudflight.jems.server.project.service.model.ProjectDescription
import io.cloudflight.jems.server.project.service.model.ProjectFull
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
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_costs.GetBudgetCostsInteractor
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCostInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
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
            isAdditionalFundAllowed = false,
            applicationFormFieldConfigurations = mutableSetOf()
        )
        private val project = ProjectFull(
            id = 1L,
            callSettings = callSettings,
            acronym = "acronym",
            applicant = user,
            duration = 12,
            programmePriority = null,
            specificObjective = null,
            projectStatus = projectStatus,
            periods = listOf(ProjectPeriod(1, 1, 1), ProjectPeriod(2, 2, 2)),
            assessmentStep1 = ProjectAssessment(
                ProjectAssessmentQuality(
                    projectId = 1L,
                    step = 1,
                    ProjectAssessmentQualityResult.NOT_RECOMMENDED,
                    updated = startDate
                ),
                ProjectAssessmentEligibility(
                    projectId = 1L,
                    step = 1,
                    ProjectAssessmentEligibilityResult.FAILED,
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
                projectBenefits = listOf(
                    ProjectRelevanceBenefit(
                        group = ProjectTargetGroupDTO.LocalPublicAuthority,
                        specification = setOf(InputTranslation(SystemLanguage.EN, "specification"))
                    )
                ),
                projectStrategies = listOf(
                    ProjectRelevanceStrategy(
                        strategy = ProgrammeStrategy.AtlanticStrategy,
                        specification = setOf(InputTranslation(SystemLanguage.EN, "specification"))
                    )
                ),
                projectSynergies = listOf(
                    ProjectRelevanceSynergy(
                        synergy = setOf(InputTranslation(SystemLanguage.EN, "synergy")),
                        specification = setOf(InputTranslation(SystemLanguage.EN, "specification"))
                    )
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
                projectJointDevelopmentDescription = setOf(
                    InputTranslation(
                        SystemLanguage.EN,
                        "projectJointDevelopmentDescription"
                    )
                ),
                projectJointImplementationDescription = setOf(
                    InputTranslation(
                        SystemLanguage.EN,
                        "projectJointImplementationDescription"
                    )
                ),
                projectJointStaffingDescription = setOf(
                    InputTranslation(
                        SystemLanguage.EN,
                        "projectJointStaffingDescription"
                    )
                ),
                projectJointFinancingDescription = setOf(
                    InputTranslation(
                        SystemLanguage.EN,
                        "projectJointFinancingDescription"
                    )
                ),
                projectHorizontalPrinciples = ProjectHorizontalPrinciples(
                    sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
                    equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.Neutral,
                    sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects
                ),
                sustainableDevelopmentDescription = setOf(
                    InputTranslation(
                        SystemLanguage.EN,
                        "sustainableDevelopmentDescription"
                    )
                ),
                equalOpportunitiesDescription = setOf(
                    InputTranslation(
                        SystemLanguage.EN,
                        "equalOpportunitiesDescription"
                    )
                ),
                sexualEqualityDescription = setOf(InputTranslation(SystemLanguage.EN, "sexualEqualityDescription"))
            ),
            projectLongTermPlans = ProjectLongTermPlans(
                projectOwnership = setOf(InputTranslation(SystemLanguage.EN, "projectOwnership")),
                projectDurability = setOf(InputTranslation(SystemLanguage.EN, "projectDurability")),
                projectTransferability = setOf(InputTranslation(SystemLanguage.EN, "projectTransferability"))
            )
        )
        private val projectPartner = ProjectPartnerDetail(
            projectId = 1,
            id = 2L,
            abbreviation = "partner",
            role = ProjectPartnerRole.LEAD_PARTNER,
            nameInOriginalLanguage = "test",
            nameInEnglish = "test",
            partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
            partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
            nace = NaceGroupLevel.A,
            otherIdentifierNumber = null,
            otherIdentifierDescription = emptySet(),
            pic = null,
            vat = "test vat",
            vatRecovery = ProjectPartnerVatRecovery.Yes,
            legalStatusId = 3L,
            addresses = listOf(
                ProjectPartnerAddress(
                    type = ProjectPartnerAddressType.Organization,
                    country = "country",
                    nutsRegion2 = "nutsRegion2",
                    nutsRegion3 = "nutsRegion3",
                    street = "street",
                    houseNumber = "houseNumber",
                    postalCode = "postalCode",
                    city = "city",
                    homepage = "homepage"
                )
            ),
            motivation = ProjectPartnerMotivation(
                organizationRelevance = setOf(InputTranslation(SystemLanguage.EN, "organizationRelevance")),
                organizationExperience = setOf(InputTranslation(SystemLanguage.EN, "organizationExperience")),
                organizationRole = setOf(InputTranslation(SystemLanguage.EN, "organizationRole"))
            )
        )
        private val partnerBudgetOptions = ProjectPartnerBudgetOptions(
            partnerId = projectPartner.id
        )
        private val partnerCoFinancing = ProjectPartnerCoFinancingAndContribution(
            finances = emptyList(),
            partnerContributions = emptyList(),
            partnerAbbreviation = projectPartner.abbreviation
        )
        private val budgetCosts = BudgetCosts(
            staffCosts = listOf(
                BudgetStaffCostEntry(
                    id = 3L,
                    numberOfUnits = BigDecimal.ONE,
                    rowSum = BigDecimal.TEN,
                    budgetPeriods = mutableSetOf(BudgetPeriod(number = 1, amount = BigDecimal.ONE)),
                    pricePerUnit = BigDecimal.TEN,
                    description = setOf(),
                    comment = setOf(InputTranslation(SystemLanguage.EN, "comment")),
                    unitType = setOf(InputTranslation(SystemLanguage.EN, "unitType")),
                    unitCostId = 4L
                )
            ),
            travelCosts = emptyList(),
            externalCosts = emptyList(),
            equipmentCosts = emptyList(),
            infrastructureCosts = emptyList(),
            unitCosts = emptyList()
        )
        private val associatedOrganization = OutputProjectAssociatedOrganizationDetail(
            id = 2L,
            partner = ProjectPartnerSummaryDTO(
                id = projectPartner.id,
                abbreviation = projectPartner.abbreviation,
                role = ProjectPartnerRoleDTO.valueOf(projectPartner.role.name),
                sortNumber = projectPartner.sortNumber,
                country = "AT"
            ),
            nameInOriginalLanguage = "nameInOriginalLanguage",
            nameInEnglish = "nameInEnglish",
            sortNumber = 1,
            address = OutputProjectAssociatedOrganizationAddress(
                country = "country",
                nutsRegion2 = "nutsRegion2",
                nutsRegion3 = "nutsRegion3",
                street = "street",
                houseNumber = "houseNumber",
                postalCode = "postalCode",
                city = "city",
                homepage = "homepage"
            ),
            contacts = listOf(
                ProjectPartnerContactDTO(
                    type = ProjectContactTypeDTO.ContactPerson,
                    title = "title",
                    firstName = "firstName",
                    lastName = "lastName",
                    email = "email",
                    telephone = "telephone"
                )
            ),
            roleDescription = setOf(InputTranslation(SystemLanguage.EN, "roleDescription"))
        )
        private val investment = WorkPackageInvestment(
            id = 2L,
            investmentNumber = 3,
            title = setOf(InputTranslation(SystemLanguage.EN, "title")),
            justificationExplanation = setOf(InputTranslation(SystemLanguage.EN, "justificationExplanation")),
            justificationTransactionalRelevance = setOf(
                InputTranslation(
                    SystemLanguage.EN,
                    "justificationTransactionalRelevance"
                )
            ),
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
            deliverables = listOf(WorkPackageActivityDeliverable()),
            partnerIds = setOf(5, 6)
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
        private val projectResult = ProjectResult(
            resultNumber = 1,
            programmeResultIndicatorId = 2L,
            programmeResultIndicatorIdentifier = "ID01",
            baseline = BigDecimal.ZERO,
            targetValue = BigDecimal.ONE,
            periodNumber = 2,
            description = setOf(InputTranslation(language = SystemLanguage.EN, translation = "description"))
        )
        private val workPackage = ProjectWorkPackageFull(
            id = 1L,
            workPackageNumber = 1,
            translatedValues = setOf(
                ProjectWorkPackageTranslatedValue(SystemLanguage.EN, "name", "objective", "audience")
            ),
            activities = listOf(activity),
            outputs = listOf(workPackageOutput),
            investments = listOf(investment)
        )
        private val projectLumpSum = ProjectLumpSum(
            programmeLumpSumId = 1L,
            period = 2,
            lumpSumContributions = listOf(ProjectPartnerLumpSum(3L, BigDecimal.ZERO))
        )
        private val programmeLumpSum = ProgrammeLumpSum(
            id = 1L,
            name = setOf(InputTranslation(SystemLanguage.EN, "name")),
            description = setOf(InputTranslation(SystemLanguage.EN, "description")),
            cost = BigDecimal.TEN,
            splittingAllowed = true,
            phase = ProgrammeLumpSumPhase.Preparation,
            categories = setOf(BudgetCategory.StaffCosts)
        )
    }

    @Test
    fun `project data provider get for project Id`() {
        val id = project.id!!
        val totalCost = BigDecimal.TEN
        every { projectPersistence.getProject(id) } returns project
        every { projectDescriptionPersistence.getProjectDescription(id) } returns projectDescription
        every { partnerPersistence.findAllByProjectId(id) } returns listOf(projectPartner)
        every { budgetOptionsPersistence.getBudgetOptions(projectPartner.id) } returns partnerBudgetOptions
        every { coFinancingPersistence.getCoFinancingAndContributions(projectPartner.id) } returns partnerCoFinancing
        every { getBudgetCosts.getBudgetCosts(projectPartner.id) } returns budgetCosts
        every { getBudgetTotalCost.getBudgetTotalCost(projectPartner.id) } returns totalCost
        every { associatedOrganizationService.findAllByProjectId(id) } returns listOf(associatedOrganization)
        every { resultPersistence.getResultsForProject(id, null) } returns listOf(projectResult)
        every { workPackagePersistence.getWorkPackagesWithAllDataByProjectId(id) } returns listOf(workPackage)
        every { projectLumpSumPersistence.getLumpSums(id) } returns listOf(projectLumpSum)
        every { programmeLumpSumPersistence.getLumpSums(listOf(projectLumpSum.programmeLumpSumId)) } returns listOf(
            programmeLumpSum
        )
        every { partnerPersistence.getPartnerStateAid(partnerId = projectPartner.id) } returns
            ProjectPartnerStateAid(
                answer1 = true,
                justification1 = setOf(InputTranslation(SystemLanguage.EN, "true")),
                answer2 = false,
                answer3 = null,
                answer4 = null
            )

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
                partners = setOf(
                    ProjectPartnerData(
                        id = projectPartner.id,
                        sortNumber = null,
                        abbreviation = projectPartner.abbreviation,
                        role = ProjectPartnerRoleData.valueOf(projectPartner.role.name),
                        nameInOriginalLanguage = projectPartner.nameInOriginalLanguage,
                        nameInEnglish = projectPartner.nameInEnglish,
                        partnerType = ProjectTargetGroupData.valueOf(projectPartner.partnerType!!.name),
                        partnerSubType = PartnerSubTypeData.LARGE_ENTERPRISE,
                        nace = NaceGroupLevelData.A,
                        otherIdentifierNumber = null,
                        otherIdentifierDescription = emptySet(),
                        pic = null,
                        vat = projectPartner.vat,
                        vatRecovery = ProjectPartnerVatRecoveryData.valueOf(projectPartner.vatRecovery!!.name),
                        legalStatusId = projectPartner.legalStatusId,
                        budget = PartnerBudgetData(
                            projectPartnerOptions = ProjectPartnerBudgetOptionsData(
                                partnerId = projectPartner.id,
                                officeAndAdministrationOnDirectCostsFlatRate = null,
                                officeAndAdministrationOnStaffCostsFlatRate = null,
                                otherCostsOnStaffCostsFlatRate = null,
                                staffCostsFlatRate = null,
                                travelAndAccommodationOnStaffCostsFlatRate = null
                            ),
                            projectPartnerCoFinancing = ProjectPartnerCoFinancingAndContributionData(
                                finances = listOf(),
                                partnerContributions = listOf(),
                                partnerAbbreviation = projectPartner.abbreviation
                            ),
                            projectPartnerBudgetCosts = BudgetCostData(
                                staffCosts = listOf(
                                    BudgetStaffCostEntryData(
                                        id = 3L,
                                        numberOfUnits = BigDecimal.ONE,
                                        rowSum = BigDecimal.TEN,
                                        budgetPeriods = mutableSetOf(
                                            BudgetPeriodData(
                                                number = 1,
                                                amount = BigDecimal.ONE
                                            )
                                        ),
                                        pricePerUnit = BigDecimal.TEN,
                                        description = setOf(),
                                        comment = setOf(InputTranslationData(SystemLanguageData.EN, "comment")),
                                        unitType = setOf(InputTranslationData(SystemLanguageData.EN, "unitType")),
                                        unitCostId = 4L
                                    )
                                ),
                                travelCosts = emptyList(),
                                externalCosts = emptyList(),
                                equipmentCosts = emptyList(),
                                infrastructureCosts = emptyList(),
                                unitCosts = emptyList()
                            ),
                            projectPartnerBudgetTotalCost = totalCost
                        ),
                        addresses = listOf(
                            ProjectPartnerAddressData(
                                type = ProjectPartnerAddressTypeData.Organization,
                                country = "country",
                                nutsRegion2 = "nutsRegion2",
                                nutsRegion3 = "nutsRegion3",
                                street = "street",
                                houseNumber = "houseNumber",
                                postalCode = "postalCode",
                                city = "city",
                                homepage = "homepage"
                            )
                        ),
                        motivation = ProjectPartnerMotivationData(
                            organizationRelevance = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "organizationRelevance"
                                )
                            ),
                            organizationExperience = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "organizationExperience"
                                )
                            ),
                            organizationRole = setOf(InputTranslationData(SystemLanguageData.EN, "organizationRole"))
                        ),
                        stateAid = ProjectPartnerStateAidData(
                            answer1 = true,
                            justification1 = setOf(InputTranslationData(SystemLanguageData.EN, "true")),
                            answer2 = false,
                            justification2 = emptySet(),
                            answer3 = null,
                            justification3 = emptySet(),
                            answer4 = null,
                            justification4 = emptySet(),
                        )
                    )
                ),
                associatedOrganisations = setOf(
                    ProjectAssociatedOrganizationData(
                        id = associatedOrganization.id,
                        partner = ProjectPartnerEssentialData(
                            id = associatedOrganization.partner.id,
                            abbreviation = associatedOrganization.partner.abbreviation,
                            role = ProjectPartnerRoleData.LEAD_PARTNER,
                            sortNumber = associatedOrganization.partner.sortNumber,
                            country = associatedOrganization.partner.country
                        ),
                        nameInOriginalLanguage = associatedOrganization.nameInOriginalLanguage,
                        nameInEnglish = associatedOrganization.nameInEnglish,
                        sortNumber = associatedOrganization.sortNumber,
                        address = ProjectAssociatedOrganizationAddressData(
                            country = associatedOrganization.address!!.country,
                            nutsRegion2 = associatedOrganization.address!!.nutsRegion2,
                            nutsRegion3 = associatedOrganization.address!!.nutsRegion3,
                            street = associatedOrganization.address!!.street,
                            houseNumber = associatedOrganization.address!!.houseNumber,
                            postalCode = associatedOrganization.address!!.postalCode,
                            city = associatedOrganization.address!!.city,
                            homepage = associatedOrganization.address!!.homepage
                        ),
                        contacts = listOf(
                            ProjectPartnerContactData(
                                type = ProjectContactTypeData.ContactPerson,
                                title = "title",
                                firstName = "firstName",
                                lastName = "lastName",
                                email = "email",
                                telephone = "telephone"
                            )
                        ),
                        roleDescription = setOf(InputTranslationData(SystemLanguageData.EN, "roleDescription"))
                    )
                )
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
                    transnationalCooperation = setOf(
                        InputTranslationData(
                            SystemLanguageData.EN,
                            "transnationalCooperation"
                        )
                    ),
                    projectBenefits = listOf(
                        ProjectRelevanceBenefitData(
                            group = ProjectTargetGroupData.LocalPublicAuthority,
                            specification = setOf(InputTranslationData(SystemLanguageData.EN, "specification"))
                        )
                    ),
                    projectStrategies = listOf(
                        ProjectRelevanceStrategyData(
                            strategy = ProgrammeStrategyData.AtlanticStrategy,
                            specification = setOf(InputTranslationData(SystemLanguageData.EN, "specification"))
                        )
                    ),
                    projectSynergies = listOf(
                        ProjectRelevanceSynergyData(
                            synergy = setOf(InputTranslationData(SystemLanguageData.EN, "synergy")),
                            specification = setOf(InputTranslationData(SystemLanguageData.EN, "specification"))
                        )
                    ),
                    availableKnowledge = setOf(InputTranslationData(SystemLanguageData.EN, "availableKnowledge"))
                ),
                projectPartnership = ProjectPartnershipData(
                    partnership = setOf(InputTranslationData(SystemLanguageData.EN, "partnership"))
                ),
                projectWorkPackages = listOf(
                    ProjectWorkPackageData(
                        id = workPackage.id,
                        workPackageNumber = workPackage.workPackageNumber,
                        name = setOf(InputTranslationData(SystemLanguageData.EN, "name")),
                        specificObjective = setOf(InputTranslationData(SystemLanguageData.EN, "objective")),
                        objectiveAndAudience = setOf(InputTranslationData(SystemLanguageData.EN, "audience")),
                        activities = listOf(
                            WorkPackageActivityData(
                                activityNumber = activity.activityNumber,
                                description = activity.translatedValues.extractField { it.description }.toDataModel(),
                                title = activity.translatedValues.extractField { it.title }.toDataModel(),
                                startPeriod = activity.startPeriod,
                                endPeriod = activity.endPeriod,
                                deliverables = listOf(
                                    WorkPackageActivityDeliverableData(
                                        deliverableNumber = 0,
                                        period = null
                                    )
                                ),
                                partnerIds = activity.partnerIds
                            )
                        ),
                        outputs = listOf(
                            WorkPackageOutputData(
                                outputNumber = workPackageOutput.outputNumber,
                                programmeOutputIndicatorId = workPackageOutput.programmeOutputIndicatorId,
                                programmeOutputIndicatorIdentifier = workPackageOutput.programmeOutputIndicatorIdentifier,
                                targetValue = workPackageOutput.targetValue,
                                periodNumber = workPackageOutput.periodNumber,
                                description = workPackageOutput.translatedValues.extractField { it.description }
                                    .toDataModel(),
                                title = workPackageOutput.translatedValues.extractField { it.title }.toDataModel()
                            )
                        ),
                        investments = listOf(
                            WorkPackageInvestmentData(
                                id = investment.id,
                                investmentNumber = investment.investmentNumber,
                                title = setOf(InputTranslationData(SystemLanguageData.EN, "title")),
                                justificationExplanation = setOf(
                                    InputTranslationData(
                                        SystemLanguageData.EN,
                                        "justificationExplanation"
                                    )
                                ),
                                justificationTransactionalRelevance = setOf(
                                    InputTranslationData(
                                        SystemLanguageData.EN,
                                        "justificationTransactionalRelevance"
                                    )
                                ),
                                justificationBenefits = setOf(
                                    InputTranslationData(
                                        SystemLanguageData.EN,
                                        "justificationBenefits"
                                    )
                                ),
                                justificationPilot = setOf(
                                    InputTranslationData(
                                        SystemLanguageData.EN,
                                        "justificationPilot"
                                    )
                                ),
                                address = WorkPackageInvestmentAddressData(
                                    "country",
                                    "reg2",
                                    "reg3",
                                    "str",
                                    "nr",
                                    "code",
                                    "city"
                                ),
                                risk = setOf(InputTranslationData(SystemLanguageData.EN, "risk")),
                                documentation = setOf(InputTranslationData(SystemLanguageData.EN, "documentation")),
                                ownershipSiteLocation = setOf(
                                    InputTranslationData(
                                        SystemLanguageData.EN,
                                        "ownershipSiteLocation"
                                    )
                                ),
                                ownershipRetain = setOf(InputTranslationData(SystemLanguageData.EN, "ownershipRetain")),
                                ownershipMaintenance = setOf(
                                    InputTranslationData(
                                        SystemLanguageData.EN,
                                        "ownershipMaintenance"
                                    )
                                )
                            )
                        )
                    )
                ),
                projectResults = listOf(
                    ProjectResultData(
                        resultNumber = projectResult.resultNumber,
                        programmeResultIndicatorId = projectResult.programmeResultIndicatorId,
                        programmeResultIndicatorIdentifier = projectResult.programmeResultIndicatorIdentifier,
                        targetValue = projectResult.targetValue,
                        periodNumber = projectResult.periodNumber,
                        description = projectResult.description.toDataModel()
                    )
                ),
                projectManagement = ProjectManagementData(
                    projectCoordination = setOf(InputTranslationData(SystemLanguageData.EN, "projectCoordination")),
                    projectQualityAssurance = setOf(
                        InputTranslationData(
                            SystemLanguageData.EN,
                            "projectQualityAssurance"
                        )
                    ),
                    projectCommunication = setOf(InputTranslationData(SystemLanguageData.EN, "projectCommunication")),
                    projectFinancialManagement = setOf(
                        InputTranslationData(
                            SystemLanguageData.EN,
                            "projectFinancialManagement"
                        )
                    ),
                    projectCooperationCriteria = ProjectCooperationCriteriaData(
                        projectJointStaffing = true,
                        projectJointImplementation = true,
                        projectJointFinancing = true,
                        projectJointDevelopment = true
                    ),
                    projectJointDevelopmentDescription = setOf(
                        InputTranslationData(
                            SystemLanguageData.EN,
                            "projectJointDevelopmentDescription"
                        )
                    ),
                    projectJointImplementationDescription = setOf(
                        InputTranslationData(
                            SystemLanguageData.EN,
                            "projectJointImplementationDescription"
                        )
                    ),
                    projectJointStaffingDescription = setOf(
                        InputTranslationData(
                            SystemLanguageData.EN,
                            "projectJointStaffingDescription"
                        )
                    ),
                    projectJointFinancingDescription = setOf(
                        InputTranslationData(
                            SystemLanguageData.EN,
                            "projectJointFinancingDescription"
                        )
                    ),
                    projectHorizontalPrinciples = ProjectHorizontalPrinciplesData(
                        sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffectData.PositiveEffects,
                        equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffectData.Neutral,
                        sexualEqualityEffect = ProjectHorizontalPrinciplesEffectData.NegativeEffects
                    ),
                    sustainableDevelopmentDescription = setOf(
                        InputTranslationData(
                            SystemLanguageData.EN,
                            "sustainableDevelopmentDescription"
                        )
                    ),
                    equalOpportunitiesDescription = setOf(
                        InputTranslationData(
                            SystemLanguageData.EN,
                            "equalOpportunitiesDescription"
                        )
                    ),
                    sexualEqualityDescription = setOf(
                        InputTranslationData(
                            SystemLanguageData.EN,
                            "sexualEqualityDescription"
                        )
                    )
                ),
                projectLongTermPlans = ProjectLongTermPlansData(
                    projectOwnership = setOf(InputTranslationData(SystemLanguageData.EN, "projectOwnership")),
                    projectDurability = setOf(InputTranslationData(SystemLanguageData.EN, "projectDurability")),
                    projectTransferability = setOf(
                        InputTranslationData(
                            SystemLanguageData.EN,
                            "projectTransferability"
                        )
                    )
                )
            )
        )
        assertThat(projectData.sectionE).isEqualTo(
            ProjectDataSectionE(
                projectLumpSums = listOf(
                    ProjectLumpSumData(
                        programmeLumpSum = ProgrammeLumpSumData(
                            id = programmeLumpSum.id,
                            name = setOf(InputTranslationData(SystemLanguageData.EN, "name")),
                            description = setOf(InputTranslationData(SystemLanguageData.EN, "description")),
                            cost = programmeLumpSum.cost,
                            splittingAllowed = programmeLumpSum.splittingAllowed,
                            phase = ProgrammeLumpSumPhaseData.Preparation,
                            categories = setOf(BudgetCategoryData.StaffCosts)
                        ),
                        period = projectLumpSum.period,
                        lumpSumContributions = listOf(ProjectPartnerLumpSumData(3L, BigDecimal.ZERO))
                    )
                )
            )
        )

        assertThat(projectData.lifecycleData).isEqualTo(
            ProjectLifecycleData(
                status = ApplicationStatusData.APPROVED
            )
        )
    }

    @Test
    fun `project data provider get for project Id - with empty values`() {
        val id = project.id!!
        every { projectPersistence.getProject(id) } returns ProjectFull(
            id = 1L,
            callSettings = callSettings,
            acronym = "acronym",
            applicant = user,
            duration = null,
            programmePriority = null,
            specificObjective = null,
            projectStatus = projectStatus,
            periods = emptyList(),
            assessmentStep1 = null,
            title = emptySet()
        )
        every { projectDescriptionPersistence.getProjectDescription(id) } returns ProjectDescription(
            projectOverallObjective = ProjectOverallObjective(overallObjective = emptySet()),
            projectRelevance = ProjectRelevance(
                territorialChallenge = emptySet(),
                commonChallenge = emptySet(),
                transnationalCooperation = emptySet(),
                projectBenefits = emptyList(),
                projectStrategies = emptyList(),
                projectSynergies = emptyList(),
                availableKnowledge = emptySet()
            ),
            projectPartnership = ProjectPartnership(partnership = emptySet()),
            projectManagement = ProjectManagement(
                projectCoordination = emptySet(),
                projectQualityAssurance = emptySet(),
                projectCommunication = emptySet(),
                projectFinancialManagement = emptySet(),
                projectCooperationCriteria = ProjectCooperationCriteria(
                    projectJointDevelopment = false,
                    projectJointFinancing = false,
                    projectJointImplementation = false,
                    projectJointStaffing = false
                ),
                projectJointDevelopmentDescription = emptySet(),
                projectJointImplementationDescription = emptySet(),
                projectJointFinancingDescription = emptySet(),
                projectHorizontalPrinciples = ProjectHorizontalPrinciples(),
                sustainableDevelopmentDescription = emptySet(),
                equalOpportunitiesDescription = emptySet(),
                sexualEqualityDescription = emptySet()
            ),
            projectLongTermPlans = ProjectLongTermPlans(
                projectOwnership = emptySet(),
                projectDurability = emptySet(),
                projectTransferability = emptySet()
            )
        )
        every { partnerPersistence.findAllByProjectId(id) } returns emptyList()
        every { associatedOrganizationService.findAllByProjectId(id) } returns emptyList()
        every { resultPersistence.getResultsForProject(id, null) } returns emptyList()
        every { workPackagePersistence.getWorkPackagesWithAllDataByProjectId(id) } returns emptyList()
        every { projectLumpSumPersistence.getLumpSums(id) } returns emptyList()

        // test getByProjectId and its mappings..
        val projectData = projectDataProvider.getProjectDataForProjectId(id)

        assertThat(projectData.sectionA).isEqualTo(
            ProjectDataSectionA(
                title = emptySet(),
                intro = emptySet(),
                acronym = "acronym",
                duration = null,
                specificObjective = null,
                programmePriority = null
            )
        )
        assertThat(projectData.sectionB).isEqualTo(
            ProjectDataSectionB(
                partners = emptySet(),
                associatedOrganisations = emptySet()
            )
        )
        assertThat(projectData.sectionC).isEqualTo(
            ProjectDataSectionC(
                projectOverallObjective = ProjectOverallObjectiveData(overallObjective = emptySet()),
                projectRelevance = ProjectRelevanceData(
                    territorialChallenge = emptySet(),
                    commonChallenge = emptySet(),
                    transnationalCooperation = emptySet(),
                    projectBenefits = emptyList(),
                    projectStrategies = emptyList(),
                    projectSynergies = emptyList(),
                    availableKnowledge = emptySet()
                ),
                projectPartnership = ProjectPartnershipData(
                    partnership = emptySet()
                ),
                projectWorkPackages = emptyList(),
                projectResults = emptyList(),
                projectManagement = ProjectManagementData(
                    projectCoordination = emptySet(),
                    projectQualityAssurance = emptySet(),
                    projectCommunication = emptySet(),
                    projectFinancialManagement = emptySet(),
                    projectCooperationCriteria = ProjectCooperationCriteriaData(
                        projectJointStaffing = false,
                        projectJointImplementation = false,
                        projectJointFinancing = false,
                        projectJointDevelopment = false
                    ),
                    projectJointDevelopmentDescription = emptySet(),
                    projectJointImplementationDescription = emptySet(),
                    projectJointStaffingDescription = emptySet(),
                    projectJointFinancingDescription = emptySet(),
                    projectHorizontalPrinciples = ProjectHorizontalPrinciplesData(null, null, null),
                    sustainableDevelopmentDescription = emptySet(),
                    equalOpportunitiesDescription = emptySet(),
                    sexualEqualityDescription = emptySet()
                ),
                projectLongTermPlans = ProjectLongTermPlansData(
                    projectOwnership = emptySet(),
                    projectDurability = emptySet(),
                    projectTransferability = emptySet()
                )
            )
        )
        assertThat(projectData.sectionE).isEqualTo(
            ProjectDataSectionE(projectLumpSums = emptyList())
        )
    }

    @Test
    fun `project data provider get fail for unknown project Id`() {
        val id = 1L
        every { projectPersistence.getProject(id) } throws ResourceNotFoundException("project")

        assertThrows<ResourceNotFoundException> { projectDataProvider.getProjectDataForProjectId(id) }
    }
}
