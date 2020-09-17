package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.description.InputProjectCooperationCriteria
import io.cloudflight.ems.api.project.dto.description.InputProjectHorizontalPrinciples
import io.cloudflight.ems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.InputProjectManagement
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevanceBenefit
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevanceStrategy
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevanceSynergy
import io.cloudflight.ems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.ems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.ems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.ems.api.strategy.ProgrammeStrategy
import io.cloudflight.ems.project.entity.description.ProjectLongTermPlans
import io.cloudflight.ems.project.entity.description.ProjectManagement
import io.cloudflight.ems.project.entity.description.ProjectOverallObjective
import io.cloudflight.ems.project.entity.description.ProjectPartnership
import io.cloudflight.ems.project.entity.description.ProjectRelevance
import io.cloudflight.ems.project.repository.description.ProjectLongTermPlansRepository
import io.cloudflight.ems.project.repository.description.ProjectManagementRepository
import io.cloudflight.ems.project.repository.description.ProjectOverallObjectiveRepository
import io.cloudflight.ems.project.repository.description.ProjectPartnershipRepository
import io.cloudflight.ems.project.repository.description.ProjectRelevanceRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectDescriptionTest {

    @MockK
    lateinit var projectOverallObjectiveRepository: ProjectOverallObjectiveRepository

    @MockK
    lateinit var projectRelevanceRepository: ProjectRelevanceRepository

    @MockK
    lateinit var projectPartnershipRepository: ProjectPartnershipRepository

    @MockK
    lateinit var projectManagementRepository: ProjectManagementRepository

    @MockK
    lateinit var projectLongTermPlansRepository: ProjectLongTermPlansRepository


    lateinit var projectDescriptionService: ProjectDescriptionService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectDescriptionService = ProjectDescriptionServiceImpl(
            projectOverallObjectiveRepository,
            projectRelevanceRepository,
            projectPartnershipRepository,
            projectManagementRepository,
            projectLongTermPlansRepository
        )
    }

    @Test
    fun updateOverallObjective() {
        every { projectOverallObjectiveRepository.save(any<ProjectOverallObjective>()) } returnsArgument 0

        assertThat(projectDescriptionService.updateOverallObjective(1L, "test value"))
            .isEqualTo("test value")

        assertThat(projectDescriptionService.updateOverallObjective(2L, null))
            .isNull()
    }

    @Test
    fun updateProjectRelevance() {
        every { projectRelevanceRepository.save(any<ProjectRelevance>()) } returnsArgument 0

        val projectRelevance = InputProjectRelevance(
            territorialChallenge = "territorial value",
            commonChallenge = "common value",
            transnationalCooperation = "transnational value",
            projectBenefits = listOf(
                InputProjectRelevanceBenefit(
                    group = ProjectTargetGroup.Egtc,
                    specification = "test Egtc"
                )
            ),
            projectStrategies = listOf(
                InputProjectRelevanceStrategy(
                    strategy = ProgrammeStrategy.AtlanticStrategy,
                    specification = "test AtlanticStrategy"
                )
            ),
            projectSynergies = listOf(InputProjectRelevanceSynergy(synergy = "syn 01", specification = "test syn 01")),
            availableKnowledge = "available knowledge value"
        )

        assertThat(projectDescriptionService.updateProjectRelevance(1L, projectRelevance))
            .overridingErrorMessage("value should be saved like it was specified")
            .isEqualTo(projectRelevance.copy())
    }

    @Test
    fun updatePartnership() {
        every { projectPartnershipRepository.save(any<ProjectPartnership>()) } returnsArgument 0

        assertThat(projectDescriptionService.updatePartnership(1L, "test value"))
            .isEqualTo("test value")

        assertThat(projectDescriptionService.updatePartnership(2L, null))
            .isNull()
    }

    @Test
    fun updateProjectManagement() {
        every { projectManagementRepository.save(any<ProjectManagement>()) } returnsArgument 0

        val cooperationCriteria = InputProjectCooperationCriteria(
            projectJointDevelopment = true,
            projectJointDevelopmentDescription = "some value",
            projectJointImplementation = false,
            projectJointImplementationDescription = null,
            projectJointStaffing = true,
            projectJointStaffingDescription = "some value",
            projectJointFinancing = false,
            projectJointFinancingDescription = ""
        )

        val horizontalPrinciples = InputProjectHorizontalPrinciples(
            sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.Neutral,
            sustainableDevelopmentDescription = "",
            equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
            equalOpportunitiesDescription = "some description",
            sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects,
            sexualEqualityDescription = null
        )

        val projectManagement = InputProjectManagement(
            projectCoordination = "coordination value",
            projectQualityAssurance = "assurance value",
            projectCommunication = "communication value",
            projectFinancialManagement = "financial value",
            projectCooperationCriteria = cooperationCriteria,
            projectHorizontalPrinciples = horizontalPrinciples
        )

        assertThat(projectDescriptionService.updateProjectManagement(1L, projectManagement))
            .overridingErrorMessage("value should be saved like it was specified")
            .isEqualTo(
                OutputProjectManagement(
                    projectCoordination = projectManagement.projectCoordination,
                    projectQualityAssurance = projectManagement.projectQualityAssurance,
                    projectCommunication = projectManagement.projectCommunication,
                    projectFinancialManagement = projectManagement.projectFinancialManagement,
                    projectCooperationCriteria = cooperationCriteria.copy(),
                    projectHorizontalPrinciples = horizontalPrinciples.copy()
                )
            )
    }

    @Test
    fun `updateProjectManagement empty horizontal and cooperation`() {
        every { projectManagementRepository.save(any<ProjectManagement>()) } returnsArgument 0

        val cooperationCriteria = InputProjectCooperationCriteria(
            projectJointDevelopment = false,
            projectJointDevelopmentDescription = null,
            projectJointImplementation = false,
            projectJointImplementationDescription = "",
            projectJointStaffing = false,
            projectJointStaffingDescription = null,
            projectJointFinancing = false,
            projectJointFinancingDescription = ""
        )

        val horizontalPrinciples = InputProjectHorizontalPrinciples(
            sustainableDevelopmentCriteriaEffect = null,
            sustainableDevelopmentDescription = "",
            equalOpportunitiesEffect = null,
            equalOpportunitiesDescription = null,
            sexualEqualityEffect = null,
            sexualEqualityDescription = ""
        )

        val projectManagement = InputProjectManagement(
            projectCoordination = "coordination value 2",
            projectQualityAssurance = "assurance value 2",
            projectCommunication = "communication value 2",
            projectFinancialManagement = "financial value 2",
            projectCooperationCriteria = cooperationCriteria,
            projectHorizontalPrinciples = horizontalPrinciples
        )

        assertThat(projectDescriptionService.updateProjectManagement(2L, projectManagement))
            .overridingErrorMessage("empty embedded classes should be omitted")
            .isEqualTo(
                OutputProjectManagement(
                    projectCoordination = projectManagement.projectCoordination,
                    projectQualityAssurance = projectManagement.projectQualityAssurance,
                    projectCommunication = projectManagement.projectCommunication,
                    projectFinancialManagement = projectManagement.projectFinancialManagement,
                    projectCooperationCriteria = null,
                    projectHorizontalPrinciples = null
                )
            )
    }

    @Test
    fun updateProjectLongTermPlans() {
        every { projectLongTermPlansRepository.save(any<ProjectLongTermPlans>()) } returnsArgument 0

        val projectLongTermPlans = InputProjectLongTermPlans(
            projectOwnership = "ownership value",
            projectDurability = null,
            projectTransferability = ""
        )

        assertThat(projectDescriptionService.updateProjectLongTermPlans(1L, projectLongTermPlans))
            .overridingErrorMessage("value should be saved like it was specified")
            .isEqualTo(
                OutputProjectLongTermPlans(
                    projectOwnership = projectLongTermPlans.projectOwnership,
                    projectDurability = projectLongTermPlans.projectDurability,
                    projectTransferability = projectLongTermPlans.projectTransferability
                )
            )
    }

}
