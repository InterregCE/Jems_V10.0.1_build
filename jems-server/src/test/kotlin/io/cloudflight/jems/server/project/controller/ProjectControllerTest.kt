package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailFormDTO
import io.cloudflight.jems.api.project.dto.ProjectPeriodDTO
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.status.ProjectDecisionDTO
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.cloudflight.jems.server.call.controller.toDTO
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudgetInteractor
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing.GetProjectBudgetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.create_project.CreateProjectInteractor
import io.cloudflight.jems.server.project.service.get_project.GetProjectInteractor
import io.cloudflight.jems.server.project.service.get_project_versions.GetProjectVersionsInteractor
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectForm
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.workpackage.investment.get_project_investment_summaries.GetProjectInvestmentSummariesInteractor
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.user.controller.toDto
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
class ProjectControllerTest {

    companion object {
        private const val projectId = 2L
        private val startDate = ZonedDateTime.now().minusDays(2)
        private val endDate = ZonedDateTime.now().plusDays(5)

        val callSettings = ProjectCallSettings(
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

        private val partner1 = ProjectPartnerSummary(
            id = 2,
            abbreviation = "Partner 1",
            role = ProjectPartnerRole.LEAD_PARTNER,
            sortNumber = 1,
            country = "AT",
        )

        private val partner2 = ProjectPartnerSummary(
            id = 1,
            abbreviation = "Partner 2",
            role = ProjectPartnerRole.PARTNER,
            sortNumber = 2,
            country = "CZ",
        )

        private val projectSummary = ProjectSummary(
            id = 8L,
            customIdentifier = "01",
            callName = "call name",
            acronym = "ACR",
            status = ApplicationStatus.SUBMITTED,
            firstSubmissionDate = ZonedDateTime.parse("2021-05-01T10:00:00+02:00"),
            lastResubmissionDate = ZonedDateTime.parse("2021-05-14T23:30:00+02:00"),
            specificObjectiveCode = "SO1.1",
            programmePriorityCode = "P1",
        )

        private val outputProjectSimple = OutputProjectSimple(
            id = 8L,
            customIdentifier = "01",
            callName = "call name",
            acronym = "ACR",
            projectStatus = ApplicationStatusDTO.SUBMITTED,
            firstSubmissionDate = ZonedDateTime.parse("2021-05-01T10:00:00+02:00"),
            lastResubmissionDate = ZonedDateTime.parse("2021-05-14T23:30:00+02:00"),
            specificObjectiveCode = "SO1.1",
            programmePriorityCode = "P1",
        )

        private val projectPeriod = ProjectPeriod(number = 1, start = 1, end = 12)
        private val projectForm = ProjectForm(
            id = projectId,
            customIdentifier = "CUST$projectId",
            callSettings = callSettings,
            acronym = "acronym",
            duration = 12,
            title = setOf(InputTranslation(SystemLanguage.EN, "title")),
            intro = setOf(InputTranslation(SystemLanguage.EN, "intro")),
            specificObjective = OutputProgrammePriorityPolicySimpleDTO(ProgrammeObjectivePolicy.AdvancedTechnologies, "code"),
            programmePriority = OutputProgrammePrioritySimple("code", setOf(InputTranslation(SystemLanguage.EN, "title"))),
            periods = listOf(projectPeriod)
        )
        private val projectDetailFormDTO = ProjectDetailFormDTO(
            id = projectId,
            customIdentifier = "CUST$projectId",
            callSettings = callSettings.toDto(),
            acronym = projectForm.acronym,
            title = projectForm.title!!,
            intro = projectForm.intro!!,
            duration = projectForm.duration,
            specificObjective = projectForm.specificObjective,
            programmePriority = projectForm.programmePriority,
            periods = listOf(ProjectPeriodDTO(projectId = projectId, number = 1, start = 1, end = 12))
        )
    }


    @RelaxedMockK
    lateinit var projectService: ProjectService

    @MockK
    lateinit var getProjectBudgetInteractor: GetProjectBudgetInteractor

    @MockK
    lateinit var getProjectBudgetCoFinancingInteractor: GetProjectBudgetCoFinancingInteractor

    @MockK
    lateinit var createProjectInteractor: CreateProjectInteractor

    @MockK
    lateinit var getProjectVersionsInteractor: GetProjectVersionsInteractor

    @MockK
    lateinit var getProjectInteractor: GetProjectInteractor

    @MockK
    lateinit var getProjectInvestmentSummariesInteractor: GetProjectInvestmentSummariesInteractor

    @InjectMockKs
    private lateinit var controller: ProjectController

    @Test
    fun getAllProjects() {
        every { getProjectInteractor.getAllProjects(any()) } returns PageImpl(listOf(projectSummary))
        assertThat(controller.getAllProjects(Pageable.unpaged()).content).containsExactly(outputProjectSimple)
    }

    @Test
    fun getMyProjects() {
        every { getProjectInteractor.getMyProjects(any()) } returns PageImpl(listOf(projectSummary))
        assertThat(controller.getMyProjects(Pageable.unpaged()).content).containsExactly(outputProjectSimple)
    }

    @Test
    fun getProjectCallSettings() {
        val callSettings = ProjectCallSettings(
            callId = 10,
            callName = "call for applications",
            startDate = startDate,
            endDate = endDate,
            endDateStep1 = null,
            lengthOfPeriod = 6,
            isAdditionalFundAllowed = false,
            flatRates = setOf(
                ProjectCallFlatRate(type = FlatRateType.STAFF_COSTS, rate = 15, adjustable = true),
            ),
            lumpSums = listOf(
                ProgrammeLumpSum(
                    id = 32,
                    name = setOf(InputTranslation(SystemLanguage.EN, "LumpSum")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "pls 32")),
                    cost = BigDecimal.TEN,
                    splittingAllowed = false,
                    phase = ProgrammeLumpSumPhase.Preparation,
                    categories = setOf(BudgetCategory.EquipmentCosts, BudgetCategory.TravelAndAccommodationCosts),
                ),
            ),
            unitCosts = listOf(
                ProgrammeUnitCost(
                    id = 4,
                    name = setOf(InputTranslation(SystemLanguage.EN, "UnitCost")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "pus 4")),
                    type = setOf(InputTranslation(SystemLanguage.EN, "type of unit cost")),
                    costPerUnit = BigDecimal.ONE,
                    isOneCostCategory = false,
                    categories = setOf(BudgetCategory.ExternalCosts, BudgetCategory.OfficeAndAdministrationCosts),
                ),
            ),
            applicationFormFieldConfigurations = mutableSetOf()
        )
        every { getProjectInteractor.getProjectCallSettings(1L) } returns callSettings
        assertThat(controller.getProjectCallSettingsById(1L)).isEqualTo(
            ProjectCallSettingsDTO(
                callId = 10,
                callName = "call for applications",
                startDate = startDate,
                endDate = endDate,
                endDateStep1 = null,
                lengthOfPeriod = 6,
                additionalFundAllowed = false,
                flatRates = FlatRateSetupDTO(
                    staffCostFlatRateSetup = FlatRateDTO(15, true),
                ),
                lumpSums = listOf(
                    ProgrammeLumpSumDTO(
                        id = 32,
                        name = setOf(InputTranslation(SystemLanguage.EN, "LumpSum")),
                        description = setOf(InputTranslation(SystemLanguage.EN, "pls 32")),
                        cost = BigDecimal.TEN,
                        splittingAllowed = false,
                        phase = ProgrammeLumpSumPhase.Preparation,
                        categories = setOf(BudgetCategory.EquipmentCosts, BudgetCategory.TravelAndAccommodationCosts),
                    ),
                ),
                unitCosts = listOf(
                    ProgrammeUnitCostDTO(
                        id = 4,
                        name = setOf(InputTranslation(SystemLanguage.EN, "UnitCost")),
                        description = setOf(InputTranslation(SystemLanguage.EN, "pus 4")),
                        type = setOf(InputTranslation(SystemLanguage.EN, "type of unit cost")),
                        costPerUnit = BigDecimal.ONE,
                        oneCostCategory = false,
                        categories = setOf(BudgetCategory.ExternalCosts, BudgetCategory.OfficeAndAdministrationCosts),
                    )
                ),
                applicationFormFieldConfigurations = mutableSetOf()
            )
        )
    }

    @Test
    fun `test partners sorting 1`() {
        val partnerBudget2 = PartnerBudget(
            partner = partner2,
            staffCosts = 4865.toScaledBigDecimal(),
            travelCosts = 9004.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            lumpSumContribution = 2787.toScaledBigDecimal(),
        )
        val partnerBudget1 = PartnerBudget(
            partner = partner1,
            staffCosts = 4865.toScaledBigDecimal(),
            travelCosts = 2000.toScaledBigDecimal(),
            externalCosts = 10000.toScaledBigDecimal(),
            equipmentCosts = 7500.toScaledBigDecimal(),
            infrastructureCosts = 2500.toScaledBigDecimal(),
            lumpSumContribution = 1213.toScaledBigDecimal(),
        )
        val projectBudgetList = listOf(partnerBudget2, partnerBudget1)

        every { getProjectBudgetInteractor.getBudget(1L) } returns projectBudgetList
        assertThat(controller.getProjectBudget(1L)).containsExactly(
            partnerBudget1.toDTO(),
            partnerBudget2.toDTO()
        )
    }

    @Test
    fun `get Project by Id`() {
        val pId = 1L
        val user = UserSummary(3L, "email", "name", "surname", UserRoleSummary(4L, "role"))
        val projectStatus = ProjectStatus(5L, ApplicationStatus.APPROVED, user, updated = startDate)
        val project = ProjectDetail(
            id = pId,
            customIdentifier = "01",
            callSettings = callSettings,
            acronym = "acronym",
            applicant = user,
            title = setOf(InputTranslation(SystemLanguage.EN, "title")),
            specificObjective = null,
            programmePriority = null,
            projectStatus = projectStatus,
            assessmentStep1 = ProjectAssessment(
                ProjectAssessmentQuality(pId, 1, ProjectAssessmentQualityResult.NOT_RECOMMENDED, updated = startDate),
                ProjectAssessmentEligibility(pId, 1, ProjectAssessmentEligibilityResult.FAILED, updated = startDate),
                eligibilityDecision = projectStatus
            )
        )
        every { getProjectInteractor.getProjectDetail(pId, null) } returns project

        assertThat(controller.getProjectById(pId)).isEqualTo(
            ProjectDetailDTO(
                id = project.id,
                customIdentifier = "01",
                callSettings = ProjectCallSettingsDTO(
                    callSettings.callId,
                    callSettings.callName,
                    callSettings.startDate,
                    callSettings.endDate,
                    callSettings.endDateStep1,
                    callSettings.lengthOfPeriod,
                    callSettings.isAdditionalFundAllowed,
                    FlatRateSetupDTO(),
                    emptyList(),
                    emptyList(),
                    callSettings.applicationFormFieldConfigurations.toDTO()
                ),
                acronym = project.acronym,
                title = project.title,
                specificObjective = project.specificObjective,
                programmePriority = project.programmePriority,
                applicant = project.applicant.toDto(),
                projectStatus = ProjectStatusDTO(
                    projectStatus.id,
                    ApplicationStatusDTO.APPROVED,
                    projectStatus.user.toDto(),
                    projectStatus.updated
                ),
                step2Active = true,
                firstStepDecision = ProjectDecisionDTO(
                    OutputProjectQualityAssessment(ProjectAssessmentQualityResult.NOT_RECOMMENDED, startDate),
                    OutputProjectEligibilityAssessment(ProjectAssessmentEligibilityResult.FAILED, startDate),
                    ProjectStatusDTO(projectStatus.id, ApplicationStatusDTO.APPROVED, user.toDto(), startDate)
                )
            )
        )
    }

    @Test
    fun `get Project Form By Id`() {
        every { getProjectInteractor.getProjectForm(projectId, null) } returns projectForm
        assertThat(controller.getProjectFormById(projectId = projectId, null))
            .isEqualTo(projectDetailFormDTO)
    }

    @Test
    fun `get Project Form By Id with previous version`() {
        val version = "3.0"
        every { getProjectInteractor.getProjectForm(projectId, version) } returns projectForm
        assertThat(controller.getProjectFormById(projectId = projectId, version))
            .isEqualTo(projectDetailFormDTO)
    }

    @Test
    fun `update Project Form`() {
        val inputData = InputProjectData(
            acronym = "acronym",
            duration = 12,
            title = setOf(InputTranslation(SystemLanguage.EN, "title")),
            intro = setOf(InputTranslation(SystemLanguage.EN, "intro")),
            specificObjective = ProgrammeObjectivePolicy.AdvancedTechnologies,
        )
        every { projectService.update(projectId, inputData) } returns projectForm
        assertThat(controller.updateProjectForm(projectId = projectId, inputData))
            .isEqualTo(projectDetailFormDTO)
    }
}
