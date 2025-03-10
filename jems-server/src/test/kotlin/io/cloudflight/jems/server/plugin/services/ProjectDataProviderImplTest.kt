package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.api.call.dto.CallType
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
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundTypeData
import io.cloudflight.jems.plugin.contract.models.programme.lumpsum.ProgrammeLumpSumData
import io.cloudflight.jems.plugin.contract.models.programme.lumpsum.ProgrammeLumpSumPhaseData
import io.cloudflight.jems.plugin.contract.models.programme.strategy.ProgrammeStrategyData
import io.cloudflight.jems.plugin.contract.models.programme.unitcost.BudgetCategoryData
import io.cloudflight.jems.plugin.contract.models.programme.unitcost.ProgrammeUnitCostListData
import io.cloudflight.jems.plugin.contract.models.project.ProjectIdentificationData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ApplicationStatusData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectAssessmentEligibilityData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectAssessmentEligibilityResultData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectAssessmentQualityData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectAssessmentQualityResultData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectDecisionData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectLifecycleData
import io.cloudflight.jems.plugin.contract.models.project.lifecycle.ProjectStatusData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.ProjectDataSectionA
import io.cloudflight.jems.plugin.contract.models.project.sectionA.ProjectPeriodData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA3.ProjectCoFinancingByFundOverviewData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA3.ProjectCoFinancingCategoryOverviewData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA3.ProjectCoFinancingOverviewData
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA4.IndicatorOverviewLine
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA4.IndicatorOverviewLineWithCodes
import io.cloudflight.jems.plugin.contract.models.project.sectionA.tableA4.ProjectResultIndicatorOverview
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
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingFundTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerSummaryData
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
import io.cloudflight.jems.plugin.contract.models.project.sectionD.BudgetCostsDetailData
import io.cloudflight.jems.plugin.contract.models.project.sectionD.ProjectBudgetOverviewPerPartnerPerPeriodData
import io.cloudflight.jems.plugin.contract.models.project.sectionD.ProjectPartnerBudgetPerPeriodData
import io.cloudflight.jems.plugin.contract.models.project.sectionD.ProjectPeriodBudgetData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.ProjectDataSectionE
import io.cloudflight.jems.plugin.contract.models.project.sectionE.lumpsum.ProjectLumpSumData
import io.cloudflight.jems.plugin.contract.models.project.sectionE.lumpsum.ProjectPartnerLumpSumData
import io.cloudflight.jems.plugin.contract.models.project.versions.ProjectVersionData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallCostOption
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeDataEntity
import io.cloudflight.jems.server.programme.repository.ProgrammeDataRepository
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.PaymentClaim
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import io.cloudflight.jems.server.programme.service.legalstatus.ProgrammeLegalStatusPersistence
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.associatedorganization.AssociatedOrganizationPersistence
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.PartnerBudgetPerPeriodCalculator
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResult
import io.cloudflight.jems.server.project.service.common.BudgetCostsCalculatorService
import io.cloudflight.jems.server.project.service.common.PartnerBudgetPerFundCalculatorService
import io.cloudflight.jems.server.project.service.contracting.model.ContractingDimensionCode
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringOption
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.model.BudgetCostsDetail
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectCooperationCriteria
import io.cloudflight.jems.server.project.service.model.ProjectDescription
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectLongTermPlans
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectOverallObjective
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerCostType
import io.cloudflight.jems.server.project.service.model.ProjectPartnership
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.model.ProjectRelevance
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceStrategy
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceSynergy
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
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
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.OutputRow
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Optional

internal class ProjectDataProviderImplTest : UnitTest() {
    @RelaxedMockK
    lateinit var callPersistence: CallPersistence

    @RelaxedMockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var projectVersionPersistence: ProjectVersionPersistence

    @RelaxedMockK
    lateinit var programmeLumpSumPersistence: ProgrammeLumpSumPersistence

    @RelaxedMockK
    lateinit var projectDescriptionPersistence: ProjectDescriptionPersistence

    @RelaxedMockK
    lateinit var workPackagePersistence: WorkPackagePersistence

    @RelaxedMockK
    lateinit var resultPersistence: ProjectResultPersistence

    @RelaxedMockK
    lateinit var partnerPersistence: PartnerPersistence

    @RelaxedMockK
    lateinit var associatedOrganizationPersistence: AssociatedOrganizationPersistence

    @RelaxedMockK
    lateinit var budgetOptionsPersistence: ProjectPartnerBudgetOptionsPersistence

    @RelaxedMockK
    lateinit var coFinancingPersistence: ProjectPartnerCoFinancingPersistence

    @RelaxedMockK
    lateinit var getBudgetCostsPersistence: ProjectPartnerBudgetCostsPersistence

    @RelaxedMockK
    lateinit var budgetCostsCalculator: BudgetCostsCalculatorService

    @RelaxedMockK
    lateinit var projectLumpSumPersistence: ProjectLumpSumPersistence

    @RelaxedMockK
    lateinit var projectResultPersistence: ProjectResultPersistence

    @RelaxedMockK
    lateinit var listOutputIndicatorsPersistence: OutputIndicatorPersistence

    @RelaxedMockK
    lateinit var listResultIndicatorsPersistence: ResultIndicatorPersistence

    @RelaxedMockK
    lateinit var partnerBudgetPerFundCalculator: PartnerBudgetPerFundCalculatorService

    @MockK
    lateinit var programmeLegalStatusPersistence: ProgrammeLegalStatusPersistence

    @MockK
    lateinit var partnerBudgetPerPeriodCalculator: PartnerBudgetPerPeriodCalculator

    @MockK
    lateinit var projectBudgetPersistence: ProjectBudgetPersistence

    @MockK
    lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistence

    @InjectMockKs
    lateinit var projectDataProvider: ProjectDataProviderImpl

    @RelaxedMockK
    lateinit var programmeDataRepository: ProgrammeDataRepository

    @MockK
    lateinit var projectUnitCostPersistence: ProjectUnitCostPersistence

    companion object {
        private val startDate = ZonedDateTime.now().minusDays(2)
        private val endDate = ZonedDateTime.now().plusDays(5)

        private val userEntity =
            UserEntity(3L, "email", false, "name", "surname", UserRoleEntity(4L, "role"), "password", UserStatus.ACTIVE)
        private val user = UserSummary(
            userEntity.id,
            userEntity.email,
            userEntity.sendNotificationsToEmail,
            userEntity.name,
            userEntity.surname,
            UserRoleSummary(4L, "role"),
            UserStatus.ACTIVE
        )
        private val projectStatus = ProjectStatus(5L, ApplicationStatus.APPROVED, user, updated = startDate)
        private val callSettings = ProjectCallSettings(
            callId = 2L,
            callName = "call",
            callType = CallType.STANDARD,
            startDate = startDate,
            endDate = endDate,
            lengthOfPeriod = 2,
            endDateStep1 = endDate,
            flatRates = emptySet(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
            stateAids = emptyList(),
            isAdditionalFundAllowed = false,
            isDirectContributionsAllowed = true,
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            costOption = CallCostOption(
                projectDefinedUnitCostAllowed = true,
                projectDefinedLumpSumAllowed = false,
            ),
            jsNotifiable = false
        )
        private val legalStatuse = listOf(
            ProgrammeLegalStatus(
                3L, ProgrammeLegalStatusType.PRIVATE, description = emptySet()
            )
        )
        private val project = ProjectFull(
            id = 1L,
            customIdentifier = "01",
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
            assessmentStep2 = ProjectAssessment(
                ProjectAssessmentQuality(
                    projectId = 1L,
                    step = 2,
                    ProjectAssessmentQualityResult.NOT_RECOMMENDED,
                    updated = startDate
                ),
                ProjectAssessmentEligibility(
                    projectId = 1L,
                    step = 2,
                    ProjectAssessmentEligibilityResult.FAILED,
                    updated = startDate
                ),
                projectStatus
            ),
            title = setOf(InputTranslation(SystemLanguage.EN, "title"))
        )
        private val projectVersions = listOf(
            ProjectVersion(
                "1.0",
                project.id!!,
                ZonedDateTime.now(),
                ApplicationStatus.SUBMITTED,
                true
            )
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
                projectSpfRecipients = emptyList(),
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
            active = true,
            abbreviation = "partner",
            role = ProjectPartnerRole.LEAD_PARTNER,
            nameInOriginalLanguage = "test",
            nameInEnglish = "test",
            createdAt = ZonedDateTime.now(),
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
        private val ERDF_FUND = ProgrammeFund(
            id = 230L,
            selected = true,
            type = ProgrammeFundType.ERDF,
        )

        private val partnerCoFinancing = ProjectPartnerCoFinancingAndContribution(
            finances = listOf(
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = ERDF_FUND,
                    percentage = BigDecimal.valueOf(6524, 2),
                ),
            ),
            partnerContributions = emptyList(),
            partnerAbbreviation = projectPartner.abbreviation
        )
        private val associatedOrganization = OutputProjectAssociatedOrganizationDetail(
            id = 2L,
            active = true,
            partner = ProjectPartnerSummaryDTO(
                id = projectPartner.id,
                abbreviation = projectPartner.abbreviation,
                institutionName = "institution",
                active = true,
                role = ProjectPartnerRoleDTO.valueOf(projectPartner.role.name),
                sortNumber = projectPartner.sortNumber,
                country = "AT",
                region = "nutsRegion3"
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
            address = Address("country", "AT","reg2", "AT01","reg3", "AT011", "str", "nr", "code", "city"),
            risk = setOf(InputTranslation(SystemLanguage.EN, "risk")),
            documentation = setOf(InputTranslation(SystemLanguage.EN, "documentation")),
            ownershipSiteLocation = setOf(InputTranslation(SystemLanguage.EN, "ownershipSiteLocation")),
            ownershipRetain = setOf(InputTranslation(SystemLanguage.EN, "ownershipRetain")),
            ownershipMaintenance = setOf(InputTranslation(SystemLanguage.EN, "ownershipMaintenance")),
            deactivated = false,
        )
        private val activity = WorkPackageActivity(
            workPackageId = 1L,
            activityNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "title")),
            description = setOf(InputTranslation(SystemLanguage.EN, "description")),
            startPeriod = 3,
            endPeriod = 4,
            deliverables = listOf(WorkPackageActivityDeliverable(deactivated = false)),
            deactivated = false,
            partnerIds = setOf(5, 6)
        )
        private val workPackageOutput = WorkPackageOutput(
            workPackageId = 1L,
            outputNumber = 0,
            programmeOutputIndicatorId = null,
            programmeOutputIndicatorIdentifier = "id",
            targetValue = BigDecimal.TEN,
            periodNumber = 1,
            title = setOf(InputTranslation(SystemLanguage.EN, "title")),
            description = setOf(InputTranslation(SystemLanguage.EN, "description")),
            programmeOutputIndicatorName = setOf(InputTranslation(SystemLanguage.EN, "programmeOutputIndicatorName")),
            programmeOutputIndicatorMeasurementUnit = setOf(
                InputTranslation(
                    SystemLanguage.EN,
                    "programmeOutputIndicatorMeasurementUnit"
                )
            ),
            periodStartMonth = 1,
            periodEndMonth = 2,
            deactivated = false,
        )
        private val projectResult = ProjectResult(
            resultNumber = 1,
            programmeResultIndicatorId = 2L,
            programmeResultIndicatorIdentifier = "ID01",
            programmeResultName = setOf(InputTranslation(language = SystemLanguage.EN, translation = "ID01 name")),
            programmeResultMeasurementUnit = setOf(
                InputTranslation(
                    language = SystemLanguage.EN,
                    translation = "ID01 measurement unit"
                )
            ),
            baseline = BigDecimal.ZERO,
            targetValue = BigDecimal.ONE,
            periodNumber = 2,
            periodStartMonth = 4,
            periodEndMonth = 6,
            description = setOf(InputTranslation(language = SystemLanguage.EN, translation = "description")),
            deactivated = false
        )
        private val workPackage = ProjectWorkPackageFull(
            id = 1L,
            workPackageNumber = 1,
            name = setOf(
                InputTranslation(SystemLanguage.EN, "name")
            ),
            specificObjective = setOf(
                InputTranslation(SystemLanguage.EN, "objective")
            ),
            objectiveAndAudience = setOf(
                InputTranslation(SystemLanguage.EN, "audience")
            ),
            activities = listOf(activity),
            outputs = listOf(workPackageOutput),
            investments = listOf(investment),
            deactivated = false
        )
        private val projectLumpSum = ProjectLumpSum(
            orderNr = 1,
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
            categories = setOf(BudgetCategory.StaffCosts),
            fastTrack = false,
            paymentClaim = PaymentClaim.IncurredByBeneficiaries
        )
        private val projectDefinedUnitCost = ProgrammeUnitCost(
            id = 51L,
            projectId = project.id,
            name = setOf(InputTranslation(SystemLanguage.ET, "name-ET")),
            description = setOf(InputTranslation(SystemLanguage.ET, "description-ET")),
            type = setOf(InputTranslation(SystemLanguage.ET, "type-ET")),
            justification = emptySet(),
            costPerUnit = BigDecimal.ONE,
            costPerUnitForeignCurrency = BigDecimal.TEN,
            foreignCurrencyCode = "CZK",
            isOneCostCategory = true,
            categories = setOf(BudgetCategory.ExternalCosts),
            paymentClaim = PaymentClaim.IncurredByBeneficiaries
        )
        private val projectDefinedUnitCostData = ProgrammeUnitCostListData(
            id = 51L,
            name = setOf(InputTranslationData(SystemLanguageData.ET, "E.2.1_name-ET")),
            type = setOf(InputTranslationData(SystemLanguageData.ET, "type-ET")),
            description = setOf(InputTranslationData(SystemLanguageData.ET, "description-ET")),
            costPerUnit = BigDecimal.ONE,
            categories = setOf(BudgetCategoryData.ExternalCosts),
        )

        // data for tableA4/output-result
        val projectOutputs = listOf(
            OutputRow(
                workPackageId = 1,
                workPackageNumber = 1,
                outputTitle = setOf(InputTranslation(SystemLanguage.EN, "outputTitle")),
                outputNumber = 1,
                outputTargetValue = BigDecimal.TEN,
                programmeOutputId = 1,
                programmeResultId = 2
            ), OutputRow(
                workPackageId = 1,
                workPackageNumber = 1,
                outputTitle = setOf(InputTranslation(SystemLanguage.EN, "outputTitle2")),
                outputNumber = 2,
                outputTargetValue = BigDecimal.ONE,
                programmeOutputId = 2,
                programmeResultId = 3
            )
        )
        val outputIndicatorSet = setOf(
            OutputIndicatorSummary(
                id = 1,
                identifier = "outputIdentifier",
                code = "outputCode",
                name = setOf(InputTranslation(SystemLanguage.EN, "outputIndicatorName")),
                programmePriorityCode = "programmePriorityCode",
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "outputIndicatorMeasurementUnit"))
            ), OutputIndicatorSummary(
                id = 2,
                identifier = "outputIdentifier2",
                code = "outputCode",
                name = setOf(InputTranslation(SystemLanguage.EN, "outputIndicatorName2")),
                programmePriorityCode = "programmePriorityCode",
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "outputIndicatorMeasurementUnit"))
            )
        )
        val resultIndicatorSet = setOf(
            ResultIndicatorSummary(
                id = 2,
                identifier = "resultIdentifier",
                code = "resultCode",
                name = setOf(InputTranslation(SystemLanguage.EN, "resultIndicatorName")),
                programmePriorityCode = "programmePriorityCode",
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "resultIndicatorMeasurementUnit")),
                baseline = BigDecimal.ONE
            ), ResultIndicatorSummary(
                id = 3,
                identifier = "resultIdentifier2",
                code = "resultCode",
                name = setOf(InputTranslation(SystemLanguage.EN, "resultIndicatorName2")),
                programmePriorityCode = "programmePriorityCode",
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "resultIndicatorMeasurementUnit")),
                baseline = BigDecimal.ONE
            )
        )
        val projectResults = listOf(
            ProjectResult(
                resultNumber = 4,
                programmeResultIndicatorId = 2,
                programmeResultIndicatorIdentifier = "programmeResultIndicatorIdentifier",
                baseline = BigDecimal.ONE,
                targetValue = BigDecimal.TEN,
                periodNumber = 1,
                description = setOf(InputTranslation(SystemLanguage.EN, "description")),
                deactivated = false
            ), ProjectResult(
                resultNumber = 5,
                programmeResultIndicatorId = 3,
                programmeResultIndicatorIdentifier = "programmeResultIndicatorIdentifier",
                baseline = BigDecimal.ONE,
                targetValue = BigDecimal.TEN,
                periodNumber = 1,
                description = setOf(InputTranslation(SystemLanguage.EN, "description2")),
                deactivated = false
            )
        )

        val contractingDimensionCodes = listOf(
            ContractingDimensionCode(
                id = 1L,
                projectId = 1L,
                programmeObjectiveDimension = ProgrammeObjectiveDimension.TypesOfIntervention,
                dimensionCode = "001",
                projectBudgetAmountShare = BigDecimal(100)
            ),
            ContractingDimensionCode(
                id = 2L,
                projectId = 1L,
                programmeObjectiveDimension = ProgrammeObjectiveDimension.RegionalAndSeaBasinStrategy,
                dimensionCode = "003",
                projectBudgetAmountShare = BigDecimal(100)
            )
        )
    }

    @Test
    fun `should return list of all project versions`() {
        every { projectVersionPersistence.getAllVersions() } returns projectVersions
        assertThat(projectDataProvider.getAllProjectVersions()).containsExactly(
            ProjectVersionData(
                projectVersions[0].version, projectVersions[0].projectId,
                projectVersions[0].createdAt, ApplicationStatusData.valueOf(projectVersions[0].status.name)
            )
        )
    }

    @Test
    fun `project data provider get for project Id`() {
        val id = project.id!!
        val budgetCostsCalculationResult = BudgetCostsCalculationResult(
            staffCosts = BigDecimal.TEN,
            totalCosts = BigDecimal.TEN,
            travelCosts = BigDecimal.ZERO,
            officeAndAdministrationCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO
        )
        val budgetOverviewPerPartnerPerPeriod = ProjectBudgetOverviewPerPartnerPerPeriod(
            partnersBudgetPerPeriod = listOf(
                ProjectPartnerBudgetPerPeriod(
                    partner = ProjectPartnerSummary(
                        id = projectPartner.id,
                        abbreviation = projectPartner.abbreviation,
                        active = projectPartner.active,
                        role = projectPartner.role,
                        sortNumber = projectPartner.sortNumber,
                        country = projectPartner.addresses.first { it.type == ProjectPartnerAddressType.Organization }.country,
                        region = projectPartner.addresses.first { it.type == ProjectPartnerAddressType.Organization }.nutsRegion2
                    ),
                    periodBudgets = mutableListOf(
                        ProjectPeriodBudget(0, 0, 0, BigDecimal.ZERO, BudgetCostsDetail(), false),
                        ProjectPeriodBudget(255, 0, 0, BigDecimal.ZERO, BudgetCostsDetail(), true)
                    ),
                    totalPartnerBudget = BigDecimal.ZERO,
                    totalPartnerBudgetDetail = BudgetCostsDetail(),
                    costType = ProjectPartnerCostType.Management
                )
            ),
            totals = listOf(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
            totalsPercentage = listOf(BigDecimal.valueOf(0, 2), BigDecimal.valueOf(0, 2), BigDecimal.valueOf(0, 2))
        )
        every { projectPersistence.getProject(id) } returns project
        every { projectVersionPersistence.getAllVersionsByProjectId(id) } returns projectVersions
        every { projectDescriptionPersistence.getProjectDescription(id) } returns projectDescription
        every { partnerPersistence.findTop50ByProjectId(id) } returns listOf(projectPartner)
        every {
            budgetOptionsPersistence.getBudgetOptions(
                setOf(projectPartner.id),
                projectPartner.projectId
            )
        } returns listOf(partnerBudgetOptions)
        every { coFinancingPersistence.getCoFinancingAndContributions(projectPartner.id) } returns partnerCoFinancing
        every { programmeLegalStatusPersistence.getMax50Statuses() } returns legalStatuse
        every { getBudgetCostsPersistence.getBudgetStaffCosts(setOf(projectPartner.id)) } returns listOf(
            BudgetStaffCostEntry(
                id = 3L,
                numberOfUnits = BigDecimal.ONE,
                rowSum = BigDecimal.TEN,
                budgetPeriods = mutableSetOf(BudgetPeriod(number = 1, amount = BigDecimal.ONE)),
                pricePerUnit = BigDecimal.TEN,
                description = setOf(),
                comments = setOf(InputTranslation(SystemLanguage.EN, "comments")),
                unitType = setOf(InputTranslation(SystemLanguage.EN, "unitType")),
                unitCostId = 4L
            )
        )
        every { getBudgetCostsPersistence.getBudgetTravelAndAccommodationCosts(setOf(projectPartner.id)) } returns emptyList()
        every { getBudgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(setOf(projectPartner.id)) } returns emptyList()
        every { getBudgetCostsPersistence.getBudgetEquipmentCosts(setOf(projectPartner.id)) } returns emptyList()
        every { getBudgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(setOf(projectPartner.id)) } returns emptyList()
        every { getBudgetCostsPersistence.getBudgetUnitCosts(setOf(projectPartner.id)) } returns emptyList()
        every { getBudgetCostsPersistence.getBudgetSpfCostTotal(projectPartner.id, null) } returns BigDecimal.valueOf(75L)
        every {
            budgetCostsCalculator.calculateCosts(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                spfCosts = BigDecimal.valueOf(75L),
            )
        } returns budgetCostsCalculationResult
        every {
            partnerBudgetPerPeriodCalculator.calculate(
                any(),
                any(),
                any(),
                any()
            )
        } returns budgetOverviewPerPartnerPerPeriod
        every { associatedOrganizationPersistence.findAllByProjectId(id) } returns listOf(associatedOrganization)
        every { resultPersistence.getResultsForProject(id, null) } returns listOf(projectResult)
        every { workPackagePersistence.getWorkPackagesWithAllDataByProjectId(id) } returns listOf(workPackage)
        every { projectLumpSumPersistence.getLumpSums(id) } returns listOf(projectLumpSum)
        every { programmeLumpSumPersistence.getLumpSums(listOf(projectLumpSum.programmeLumpSumId)) } returns listOf(
            programmeLumpSum
        )
        every { projectUnitCostPersistence.getProjectUnitCostList(id, null) } returns listOf(
            projectDefinedUnitCost
        )
        every { partnerPersistence.getPartnerStateAid(partnerId = projectPartner.id) } returns
            ProjectPartnerStateAid(
                answer1 = true,
                justification1 = setOf(InputTranslation(SystemLanguage.EN, "true")),
                answer2 = false,
                answer3 = null,
                answer4 = null,
                stateAidScheme = null
            )
        every { coFinancingPersistence.getAvailableFunds(projectPartner.id) } returns setOf(ERDF_FUND)
        // data for tableA4/output-result
        every { workPackagePersistence.getAllOutputsForProjectIdSortedByNumbers(id) } returns projectOutputs
        every { listOutputIndicatorsPersistence.getTop250OutputIndicators() } returns outputIndicatorSet
        every { listResultIndicatorsPersistence.getTop50ResultIndicators() } returns resultIndicatorSet
        every { projectResultPersistence.getResultsForProject(id, null) } returns projectResults
        every { projectBudgetPersistence.getBudgetPerPartner(setOf(2), id, null) } returns emptyList()
        every { projectBudgetPersistence.getBudgetTotalForPartners(setOf(2), id, null) } returns emptyMap()
        val programmeData = mockk<ProgrammeDataEntity>()
        every { programmeData.title } returns "programme title"
        every { programmeDataRepository.findById(1L) } returns Optional.of(programmeData)
        every { contractingMonitoringPersistence.getContractingMonitoring(1L).dimensionCodes } returns contractingDimensionCodes

        // test getByProjectId and its mappings..
        val projectData = projectDataProvider.getProjectDataForProjectId(id)

        assertThat(projectData.sectionA).isEqualTo(
            ProjectDataSectionA(
                customIdentifier = "01",
                title = setOf(InputTranslationData(SystemLanguageData.EN, "title")),
                intro = emptySet(),
                acronym = project.acronym,
                duration = project.duration,
                specificObjective = project.specificObjective?.toDataModel(),
                programmePriority = project.programmePriority?.toDataModel(),
                coFinancingOverview = ProjectCoFinancingOverviewData(
                    projectManagementCoFinancing = ProjectCoFinancingCategoryOverviewData(
                        fundOverviews = listOf(
                            ProjectCoFinancingByFundOverviewData(
                                fundId = 230L,
                                fundType = ProgrammeFundTypeData.ERDF,
                                fundAbbreviation = emptySet(),
                                fundingAmount = BigDecimal.valueOf(6_52, 2),
                                coFinancingRate = BigDecimal.valueOf(100_00, 2),
                                autoPublicContribution = BigDecimal.ZERO,
                                otherPublicContribution = BigDecimal.ZERO,
                                totalPublicContribution = BigDecimal.ZERO,
                                privateContribution = BigDecimal.ZERO,
                                totalContribution = BigDecimal.ZERO,
                                totalFundAndContribution = BigDecimal.valueOf(6_52, 2),
                            )
                        ),
                        totalFundingAmount = BigDecimal.valueOf(6_52, 2),
                        totalEuFundingAmount = BigDecimal.valueOf(6_52, 2),
                        averageCoFinancingRate = BigDecimal.valueOf(65_20, 2),
                        averageEuFinancingRate = BigDecimal.valueOf(100_00, 2),

                        totalAutoPublicContribution = BigDecimal.ZERO,
                        totalEuAutoPublicContribution = BigDecimal.ZERO,
                        totalOtherPublicContribution = BigDecimal.ZERO,
                        totalEuOtherPublicContribution = BigDecimal.ZERO,
                        totalPublicContribution = BigDecimal.ZERO,
                        totalEuPublicContribution = BigDecimal.ZERO,
                        totalPrivateContribution = BigDecimal.ZERO,
                        totalEuPrivateContribution = BigDecimal.ZERO,
                        totalContribution = BigDecimal.ZERO,
                        totalEuContribution = BigDecimal.ZERO,

                        totalFundAndContribution = BigDecimal.valueOf(10_00L, 2),
                        totalEuFundAndContribution = BigDecimal.valueOf(6_52, 2),
                    ),
                    projectSpfCoFinancing = ProjectCoFinancingCategoryOverviewData()
                ),
                resultIndicatorOverview = ProjectResultIndicatorOverview(
                    indicatorLines = listOf(
                        IndicatorOverviewLine(
                            outputIndicatorId = 1L,
                            outputIndicatorIdentifier = "outputIdentifier",
                            outputIndicatorName = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "outputIndicatorName"
                                )
                            ),
                            outputIndicatorMeasurementUnit = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "outputIndicatorMeasurementUnit"
                                )
                            ),
                            outputIndicatorTargetValueSumUp = BigDecimal.TEN,
                            projectOutputNumber = "1.1",
                            projectOutputTitle = setOf(InputTranslationData(SystemLanguageData.EN, "outputTitle")),
                            projectOutputTargetValue = BigDecimal.TEN,
                            resultIndicatorId = 2L,
                            resultIndicatorIdentifier = "resultIdentifier",
                            resultIndicatorName = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "resultIndicatorName"
                                )
                            ),
                            resultIndicatorMeasurementUnit = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "resultIndicatorMeasurementUnit"
                                )
                            ),
                            resultIndicatorBaseline = setOf(BigDecimal.ONE),
                            resultIndicatorTargetValueSumUp = BigDecimal.TEN,
                            onlyResultWithoutOutputs = false,
                        ),
                        IndicatorOverviewLine(
                            outputIndicatorId = 2L,
                            outputIndicatorIdentifier = "outputIdentifier2",
                            outputIndicatorName = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "outputIndicatorName2"
                                )
                            ),
                            outputIndicatorMeasurementUnit = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "outputIndicatorMeasurementUnit"
                                )
                            ),
                            outputIndicatorTargetValueSumUp = BigDecimal.ONE,
                            projectOutputNumber = "1.2",
                            projectOutputTitle = setOf(InputTranslationData(SystemLanguageData.EN, "outputTitle2")),
                            projectOutputTargetValue = BigDecimal.ONE,
                            resultIndicatorId = 3L,
                            resultIndicatorIdentifier = "resultIdentifier2",
                            resultIndicatorName = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "resultIndicatorName2"
                                )
                            ),
                            resultIndicatorMeasurementUnit = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "resultIndicatorMeasurementUnit"
                                )
                            ),
                            resultIndicatorBaseline = setOf(BigDecimal.ONE),
                            resultIndicatorTargetValueSumUp = BigDecimal.TEN,
                            onlyResultWithoutOutputs = false,
                        )
                    ),
                    indicatorLinesWithCodes = listOf(
                        IndicatorOverviewLineWithCodes(
                            outputIndicatorId = 1L,
                            outputIndicatorIdentifier = "outputIdentifier",
                            outputIndicatorName = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "outputIndicatorName"
                                )
                            ),
                            outputIndicatorMeasurementUnit = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "outputIndicatorMeasurementUnit"
                                )
                            ),
                            outputIndicatorTargetValueSumUp = BigDecimal.TEN,
                            projectOutputNumber = "1.1",
                            projectOutputTitle = setOf(InputTranslationData(SystemLanguageData.EN, "outputTitle")),
                            projectOutputTargetValue = BigDecimal.TEN,
                            resultIndicatorId = 2L,
                            resultIndicatorIdentifier = "resultIdentifier",
                            resultIndicatorName = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "resultIndicatorName"
                                )
                            ),
                            resultIndicatorMeasurementUnit = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "resultIndicatorMeasurementUnit"
                                )
                            ),
                            resultIndicatorBaseline = setOf(BigDecimal.ONE),
                            resultIndicatorTargetValueSumUp = BigDecimal.TEN,
                            onlyResultWithoutOutputs = false,
                            outputIndicatorCode = "outputCode",
                            resultIndicatorCode = "resultCode"
                        ),
                        IndicatorOverviewLineWithCodes(
                            outputIndicatorId = 2L,
                            outputIndicatorIdentifier = "outputIdentifier2",
                            outputIndicatorName = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "outputIndicatorName2"
                                )
                            ),
                            outputIndicatorMeasurementUnit = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "outputIndicatorMeasurementUnit"
                                )
                            ),
                            outputIndicatorTargetValueSumUp = BigDecimal.ONE,
                            projectOutputNumber = "1.2",
                            projectOutputTitle = setOf(InputTranslationData(SystemLanguageData.EN, "outputTitle2")),
                            projectOutputTargetValue = BigDecimal.ONE,
                            resultIndicatorId = 3L,
                            resultIndicatorIdentifier = "resultIdentifier2",
                            resultIndicatorName = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "resultIndicatorName2"
                                )
                            ),
                            resultIndicatorMeasurementUnit = setOf(
                                InputTranslationData(
                                    SystemLanguageData.EN,
                                    "resultIndicatorMeasurementUnit"
                                )
                            ),
                            resultIndicatorBaseline = setOf(BigDecimal.ONE),
                            resultIndicatorTargetValueSumUp = BigDecimal.TEN,
                            onlyResultWithoutOutputs = false,
                            outputIndicatorCode = "outputCode",
                            resultIndicatorCode = "resultCode"
                        )
                    )
                ),
                periods = listOf(ProjectPeriodData(1, 1, 1), ProjectPeriodData(2, 2, 2))
            )
        )
        assertThat(projectData.sectionB).isEqualTo(
            ProjectDataSectionB(
                partners = setOf(
                    ProjectPartnerData(
                        id = projectPartner.id,
                        active = true,
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
                                finances = listOf(
                                    ProjectPartnerCoFinancingData(
                                        fundType = ProjectPartnerCoFinancingFundTypeData.MainFund,
                                        fund = ProgrammeFundData(
                                            id = ERDF_FUND.id,
                                            selected = true,
                                            type = ProgrammeFundTypeData.ERDF,
                                        ),
                                        percentage = BigDecimal.valueOf(6524, 2),
                                    )
                                ),
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
                                        comments = setOf(InputTranslationData(SystemLanguageData.EN, "comments")),
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
                            projectPartnerBudgetTotalCost = budgetCostsCalculationResult.totalCosts,
                            projectBudgetCostsCalculationResult = budgetCostsCalculationResult.toDataModel()
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
                            stateAidScheme = null
                        )
                    )
                ),
                associatedOrganisations = setOf(
                    ProjectAssociatedOrganizationData(
                        id = associatedOrganization.id,
                        active = true,
                        partner = ProjectPartnerEssentialData(
                            id = associatedOrganization.partner.id,
                            active = true,
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
                        deactivated = false,
                        workPackageNumber = workPackage.workPackageNumber,
                        name = setOf(InputTranslationData(SystemLanguageData.EN, "name")),
                        specificObjective = setOf(InputTranslationData(SystemLanguageData.EN, "objective")),
                        objectiveAndAudience = setOf(InputTranslationData(SystemLanguageData.EN, "audience")),
                        activities = listOf(
                            WorkPackageActivityData(
                                activityNumber = activity.activityNumber,
                                description = activity.description.toDataModel(),
                                title = activity.title.toDataModel(),
                                startPeriod = activity.startPeriod,
                                endPeriod = activity.endPeriod,
                                deliverables = listOf(
                                    WorkPackageActivityDeliverableData(
                                        deliverableNumber = 0,
                                        period = null,
                                        deactivated = false
                                    )
                                ),
                                partnerIds = activity.partnerIds,
                                deactivated = false
                            )
                        ),
                        outputs = listOf(
                            WorkPackageOutputData(
                                outputNumber = workPackageOutput.outputNumber,
                                programmeOutputIndicatorId = workPackageOutput.programmeOutputIndicatorId,
                                programmeOutputIndicatorIdentifier = workPackageOutput.programmeOutputIndicatorIdentifier,
                                targetValue = workPackageOutput.targetValue,
                                periodNumber = workPackageOutput.periodNumber,
                                description = workPackageOutput.description.toDataModel(),
                                title = workPackageOutput.title.toDataModel(),
                                periodStartMonth = workPackageOutput.periodStartMonth,
                                periodEndMonth = workPackageOutput.periodEndMonth,
                                programmeOutputIndicatorName = workPackageOutput.programmeOutputIndicatorName.toDataModel(),
                                programmeOutputIndicatorMeasurementUnit = workPackageOutput.programmeOutputIndicatorMeasurementUnit.toDataModel(),
                                deactivated = workPackageOutput.deactivated
                            )
                        ),
                        investments = listOf(
                            WorkPackageInvestmentData(
                                id = investment.id,
                                investmentNumber = investment.investmentNumber,
                                title = setOf(InputTranslationData(SystemLanguageData.EN, "title")),
                                expectedDeliveryPeriod = investment.expectedDeliveryPeriod,
                                deactivated = false,
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
                        programmeResultName = setOf(InputTranslationData(SystemLanguageData.EN, "ID01 name")),
                        programmeResultMeasurementUnit = setOf(InputTranslationData(SystemLanguageData.EN, "ID01 measurement unit")),
                        baseline = BigDecimal.ZERO,
                        targetValue = projectResult.targetValue,
                        periodNumber = projectResult.periodNumber,
                        periodStartMonth = projectResult.periodStartMonth,
                        periodEndMonth = projectResult.periodEndMonth,
                        description = projectResult.description.toDataModel(),
                        deactivated = false
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
                ),
                projectDefinedUnitCosts = listOf(projectDefinedUnitCostData),
            )
        )

        val allZeroCostDetailData = BudgetCostsDetailData(
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        )
        assertThat(projectData.sectionD.projectPartnerBudgetPerPeriodData).isEqualTo(
            ProjectBudgetOverviewPerPartnerPerPeriodData(
                partnersBudgetPerPeriod = listOf(
                    ProjectPartnerBudgetPerPeriodData(
                        partner = ProjectPartnerSummaryData(
                            projectPartner.id,
                            projectPartner.active,
                            projectPartner.abbreviation,
                            ProjectPartnerRoleData.valueOf(projectPartner.role.name),
                            projectPartner.sortNumber,
                            projectPartner.addresses.first { it.type == ProjectPartnerAddressType.Organization }.country,
                            projectPartner.addresses.first { it.type == ProjectPartnerAddressType.Organization }.nutsRegion2,
                            otherIdentifierDescription = null
                        ),
                        periodBudgets = mutableListOf(
                            ProjectPeriodBudgetData(0, 0, 0, BigDecimal.ZERO, allZeroCostDetailData, false),
                            ProjectPeriodBudgetData(255, 0, 0, BigDecimal.ZERO, allZeroCostDetailData, true)
                        ),
                        totalPartnerBudget = BigDecimal.ZERO,
                        totalPartnerBudgetDetail = allZeroCostDetailData
                    )
                ),
                totals = listOf(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                totalsPercentage = listOf(BigDecimal.valueOf(0, 2), BigDecimal.valueOf(0, 2), BigDecimal.valueOf(0, 2))
            )
        )

        assertThat(projectData.lifecycleData).isEqualTo(
            ProjectLifecycleData(
                status = ApplicationStatusData.APPROVED,
                submissionDateStepOne = null,
                firstSubmissionDate = project.firstSubmission?.updated,
                lastResubmissionDate = project.lastResubmission?.updated,
                assessmentStep1 = ProjectDecisionData(
                    assessmentQuality = ProjectAssessmentQualityData(
                        projectId = project.assessmentStep1?.assessmentQuality?.projectId!!,
                        step = project.assessmentStep1?.assessmentQuality?.step!!,
                        result = ProjectAssessmentQualityResultData.valueOf(project.assessmentStep1?.assessmentQuality?.result?.name!!),
                        updated = project.assessmentStep1?.assessmentQuality?.updated,
                        note = project.assessmentStep1?.assessmentQuality?.note

                    ),
                    assessmentEligibility = ProjectAssessmentEligibilityData(
                        projectId = project.id!!,
                        step = project.assessmentStep1?.assessmentEligibility?.step!!,
                        result = ProjectAssessmentEligibilityResultData.valueOf(project.assessmentStep1?.assessmentEligibility?.result?.name!!),
                        updated = project.assessmentStep1?.assessmentEligibility?.updated,
                        note = project.assessmentStep1?.assessmentEligibility?.note
                    ),
                    eligibilityDecision = project.assessmentStep1?.eligibilityDecision?.toData(),
                    preFundingDecision = project.assessmentStep1?.preFundingDecision?.toData(),
                    fundingDecision = project.assessmentStep1?.fundingDecision?.toData(),
                    modificationDecision = project.assessmentStep1?.modificationDecision?.toData()
                ),
                assessmentStep2 = ProjectDecisionData(
                    assessmentQuality = ProjectAssessmentQualityData(
                        projectId = project.assessmentStep2?.assessmentQuality?.projectId!!,
                        step = project.assessmentStep2?.assessmentQuality?.step!!,
                        result = ProjectAssessmentQualityResultData.valueOf(project.assessmentStep2?.assessmentQuality?.result?.name!!),
                        updated = project.assessmentStep2?.assessmentQuality?.updated,
                        note = project.assessmentStep2?.assessmentQuality?.note

                    ),
                    assessmentEligibility = ProjectAssessmentEligibilityData(
                        projectId = project.id!!,
                        step = project.assessmentStep2?.assessmentEligibility?.step!!,
                        result = ProjectAssessmentEligibilityResultData.valueOf(project.assessmentStep2?.assessmentEligibility?.result?.name!!),
                        updated = project.assessmentStep2?.assessmentEligibility?.updated,
                        note = project.assessmentStep2?.assessmentEligibility?.note
                    ),
                    eligibilityDecision = project.assessmentStep2?.eligibilityDecision?.toData(),
                    preFundingDecision = project.assessmentStep2?.preFundingDecision?.toData(),
                    fundingDecision = project.assessmentStep2?.fundingDecision?.toData(),
                    modificationDecision = project.assessmentStep2?.modificationDecision?.toData()
                )
            )
        )
        assertThat(projectData.programmeTitle).isEqualTo("programme title")
    }

    @Test
    fun `project data provider get for project Id - with empty values`() {
        val id = project.id!!
        every { contractingMonitoringPersistence.getContractingMonitoring(id).dimensionCodes } returns emptyList()
        every { projectPersistence.getProject(id) } returns ProjectFull(
            id = 1L,
            customIdentifier = "01",
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
                projectSpfRecipients = emptyList(),
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
        every { partnerPersistence.findTop50ByProjectId(id) } returns emptyList()
        every { associatedOrganizationPersistence.findAllByProjectId(id) } returns emptyList()
        every { resultPersistence.getResultsForProject(id, null) } returns emptyList()
        every { workPackagePersistence.getWorkPackagesWithAllDataByProjectId(id) } returns emptyList()
        every { projectLumpSumPersistence.getLumpSums(id) } returns emptyList()
        every { programmeLegalStatusPersistence.getMax50Statuses() } returns legalStatuse
        // data for tableA4/output-result
        every { workPackagePersistence.getAllOutputsForProjectIdSortedByNumbers(id) } returns emptyList()
        every { listOutputIndicatorsPersistence.getTop250OutputIndicators() } returns emptySet()
        every { listResultIndicatorsPersistence.getTop50ResultIndicators() } returns emptySet()
        every { projectResultPersistence.getResultsForProject(id, null) } returns emptyList()
        every { projectBudgetPersistence.getBudgetPerPartner(emptySet(), id, null) } returns emptyList()
        every { projectBudgetPersistence.getBudgetTotalForPartners(emptySet(), id, null) } returns emptyMap()

        // test getByProjectId and its mappings..
        val projectData = projectDataProvider.getProjectDataForProjectId(id)

        assertThat(projectData.sectionA).isEqualTo(
            ProjectDataSectionA(
                customIdentifier = "01",
                title = emptySet(),
                intro = emptySet(),
                acronym = "acronym",
                duration = null,
                specificObjective = null,
                programmePriority = null,
                coFinancingOverview = ProjectCoFinancingOverviewData(
                    projectManagementCoFinancing = ProjectCoFinancingCategoryOverviewData(
                        fundOverviews = emptyList(),
                        totalFundingAmount = BigDecimal.ZERO,
                        totalEuFundingAmount = BigDecimal.ZERO,
                        averageCoFinancingRate = BigDecimal.ZERO,
                        averageEuFinancingRate = BigDecimal.ZERO,

                        totalAutoPublicContribution = BigDecimal.ZERO,
                        totalEuAutoPublicContribution = BigDecimal.ZERO,
                        totalOtherPublicContribution = BigDecimal.ZERO,
                        totalEuOtherPublicContribution = BigDecimal.ZERO,
                        totalPublicContribution = BigDecimal.ZERO,
                        totalEuPublicContribution = BigDecimal.ZERO,
                        totalPrivateContribution = BigDecimal.ZERO,
                        totalEuPrivateContribution = BigDecimal.ZERO,
                        totalContribution = BigDecimal.ZERO,
                        totalEuContribution = BigDecimal.ZERO,

                        totalFundAndContribution = BigDecimal.ZERO,
                        totalEuFundAndContribution = BigDecimal.ZERO,
                    ),
                    projectSpfCoFinancing = ProjectCoFinancingCategoryOverviewData()
                ),
                resultIndicatorOverview = ProjectResultIndicatorOverview(emptyList(), emptyList())
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
            ProjectDataSectionE(projectLumpSums = emptyList(), projectDefinedUnitCosts = listOf(projectDefinedUnitCostData))
        )

        assertThat(projectData.versions).isEqualTo(projectVersions.toDataModel())

    }

    @Test
    fun `project data provider get fail for unknown project Id`() {
        val id = 1L
        every { projectPersistence.getProject(id) } throws ResourceNotFoundException("project")

        assertThrows<ResourceNotFoundException> { projectDataProvider.getProjectDataForProjectId(id) }
    }

    @Test
    fun `should return list of project versions`() {
        val createdAt = ZonedDateTime.now()
        val versions = listOf(
            ProjectVersion("1.0", 1L, createdAt, ApplicationStatus.DRAFT, true)
        )
        val versionsData = listOf(
            ProjectVersionData("1.0", 1L, createdAt, ApplicationStatusData.DRAFT)
        )
        every { projectVersionPersistence.getAllVersions() } returns versions

        assertThat(projectDataProvider.getAllProjectVersions()).isEqualTo(versionsData)
    }


    @Test
    fun getProjectIdentificationData() {
        val projectId = 1L
        val projectStartDate = LocalDate.of(2023, 1, 19)
        val contractMonitoring = ProjectContractingMonitoring(
            projectId = projectId,
            startDate = projectStartDate,
            closureDate = mockk(),
            lastPaymentDates = mockk(),
            endDate = null,
            typologyProv94 = ContractingMonitoringExtendedOption.Yes,
            typologyProv94Comment = null,
            typologyProv95= ContractingMonitoringExtendedOption.No,
            typologyProv95Comment= null,
            typologyStrategic= ContractingMonitoringOption.Yes,
            typologyStrategicComment= null,
            typologyPartnership= ContractingMonitoringOption.No,
            typologyPartnershipComment= null,
            addDates= emptyList(),
            dimensionCodes = emptyList(),
            fastTrackLumpSums = emptyList()
        )

        every { projectVersionPersistence.getLatestApprovedOrCurrent(projectId)} returns "1.0"
        every { projectPersistence.getProject(projectId,"1.0") } returns ProjectFull(
            id = 1L,
            customIdentifier = "identifier",
            callSettings = callSettings,
            acronym = "acronym",
            applicant = user,
            duration = 12,
            programmePriority = null,
            specificObjective = null,
            projectStatus = projectStatus,
            periods = emptyList(),
            assessmentStep1 = null,
            title = emptySet()
        )

        every { contractingMonitoringPersistence.getContractingMonitoring(1L) } returns contractMonitoring
        val programmeData = mockk<ProgrammeDataEntity>()
        every { programmeData.title } returns "programme title"
        every { programmeDataRepository.findById(1L) } returns Optional.of(programmeData)


        assertThat(projectDataProvider.getProjectIdentificationData(projectId)).isEqualTo(
            ProjectIdentificationData(
                id = projectId,
                customIdentifier = "identifier",
                acronym = "acronym",
                status = ApplicationStatusData.APPROVED,
                title = emptySet(),
                intro = emptySet(),
                durationInMonths = 12,
                projectStartDate = projectStartDate,
                projectEndDate = LocalDate.of(2024, 1, 18),
                programmeTitle = "programme title",
                programmePriorityCode = null,
                lifecycleData = ProjectLifecycleData(
                    status = project.projectStatus.status.toDataModel(),
                    submissionDateStepOne = null,
                    firstSubmissionDate = null,
                    lastResubmissionDate = null,
                    contractedDate = null,
                    assessmentStep1 = null,
                    assessmentStep2 = null
                )
            )
        )

    }

    private fun ProjectStatus.toData() =
        ProjectStatusData(
            id,
            ApplicationStatusData.valueOf(status.name),
            updated,
            decisionDate,
            entryIntoForceDate,
            note
        )
}
