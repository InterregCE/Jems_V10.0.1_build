package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.description.InputProjectCooperationCriteria
import io.cloudflight.ems.api.project.dto.description.InputProjectHorizontalPrinciples
import io.cloudflight.ems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.InputProjectManagement
import io.cloudflight.ems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.ems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevanceBenefit
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevanceStrategy
import io.cloudflight.ems.api.project.dto.description.InputProjectRelevanceSynergy
import io.cloudflight.ems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.ems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.ems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.ems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.ems.api.strategy.ProgrammeStrategy
import io.cloudflight.ems.project.entity.description.ProjectCooperationCriteria
import io.cloudflight.ems.project.entity.description.ProjectHorizontalPrinciples
import io.cloudflight.ems.project.entity.description.ProjectLongTermPlans
import io.cloudflight.ems.project.entity.description.ProjectManagement
import io.cloudflight.ems.project.entity.description.ProjectOverallObjective
import io.cloudflight.ems.project.entity.description.ProjectPartnership
import io.cloudflight.ems.project.entity.description.ProjectRelevance
import io.cloudflight.ems.project.entity.description.ProjectRelevanceBenefit
import io.cloudflight.ems.project.entity.description.ProjectRelevanceStrategy
import io.cloudflight.ems.project.entity.description.ProjectRelevanceSynergy
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

class ProjectDescriptionServiceTest {

    companion object {
        private val projectRelevance = InputProjectRelevance(
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

        private val projectRelevanceEntity = ProjectRelevance(
            projectId = 1,
            territorialChallenge = projectRelevance.territorialChallenge,
            commonChallenge = projectRelevance.commonChallenge,
            transnationalCooperation = projectRelevance.transnationalCooperation,
            projectBenefits = setOf(
                ProjectRelevanceBenefit(
                    targetGroup = ProjectTargetGroup.Egtc,
                    specification = "test Egtc"
                )
            ),
            projectStrategies = setOf(
                ProjectRelevanceStrategy(
                    strategy = ProgrammeStrategy.AtlanticStrategy,
                    specification = "test AtlanticStrategy"
                )
            ),
            projectSynergies = setOf(ProjectRelevanceSynergy(synergy = "syn 01", specification = "test syn 01")),
            availableKnowledge = projectRelevance.availableKnowledge
        )

        private val cooperationCriteria = InputProjectCooperationCriteria(
            projectJointDevelopment = true,
            projectJointDevelopmentDescription = "some value",
            projectJointImplementation = false,
            projectJointImplementationDescription = null,
            projectJointStaffing = true,
            projectJointStaffingDescription = "some value",
            projectJointFinancing = false,
            projectJointFinancingDescription = ""
        )

        private val cooperationCriteriaEntity = ProjectCooperationCriteria(
            projectJointDevelopment = cooperationCriteria.projectJointDevelopment,
            projectJointDevelopmentDescription = cooperationCriteria.projectJointDevelopmentDescription,
            projectJointImplementation = cooperationCriteria.projectJointImplementation,
            projectJointImplementationDescription = cooperationCriteria.projectJointImplementationDescription,
            projectJointStaffing = cooperationCriteria.projectJointStaffing,
            projectJointStaffingDescription = cooperationCriteria.projectJointStaffingDescription,
            projectJointFinancing = cooperationCriteria.projectJointFinancing,
            projectJointFinancingDescription = cooperationCriteria.projectJointFinancingDescription
        )

        private val horizontalPrinciples = InputProjectHorizontalPrinciples(
            sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.Neutral,
            sustainableDevelopmentDescription = "",
            equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
            equalOpportunitiesDescription = "some description",
            sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects,
            sexualEqualityDescription = null
        )

        private val horizontalPrinciplesEntity = ProjectHorizontalPrinciples(
            sustainableDevelopmentCriteriaEffect = horizontalPrinciples.sustainableDevelopmentCriteriaEffect,
            sustainableDevelopmentDescription = horizontalPrinciples.sustainableDevelopmentDescription,
            equalOpportunitiesEffect = horizontalPrinciples.equalOpportunitiesEffect,
            equalOpportunitiesDescription = horizontalPrinciples.equalOpportunitiesDescription,
            sexualEqualityEffect = horizontalPrinciples.sexualEqualityEffect,
            sexualEqualityDescription = horizontalPrinciples.sexualEqualityDescription
        )

        private val projectManagement = InputProjectManagement(
            projectCoordination = "coordination value",
            projectQualityAssurance = "assurance value",
            projectCommunication = "communication value",
            projectFinancialManagement = "financial value",
            projectCooperationCriteria = cooperationCriteria,
            projectHorizontalPrinciples = horizontalPrinciples
        )

        private val projectManagementEntity = ProjectManagement(
            projectId = 1,
            projectCoordination = projectManagement.projectCoordination,
            projectQualityAssurance = projectManagement.projectQualityAssurance,
            projectCommunication = projectManagement.projectCommunication,
            projectFinancialManagement = projectManagement.projectFinancialManagement,
            projectCooperationCriteria = cooperationCriteriaEntity,
            projectHorizontalPrinciples = horizontalPrinciplesEntity
        )

        private val outputProjectManagement = OutputProjectManagement(
            projectCoordination = projectManagement.projectCoordination,
            projectQualityAssurance = projectManagement.projectQualityAssurance,
            projectCommunication = projectManagement.projectCommunication,
            projectFinancialManagement = projectManagement.projectFinancialManagement,
            projectCooperationCriteria = cooperationCriteria.copy(),
            projectHorizontalPrinciples = horizontalPrinciples.copy()
        )

        private val projectLongTermPlans = InputProjectLongTermPlans(
            projectOwnership = "ownership value",
            projectDurability = null,
            projectTransferability = ""
        )

        private val projectLongTermPlansEntity = ProjectLongTermPlans(
            projectId = 1,
            projectOwnership = projectLongTermPlans.projectOwnership,
            projectDurability = projectLongTermPlans.projectDurability,
            projectTransferability = projectLongTermPlans.projectTransferability
        )

        private val outputProjectLongTermPlans = OutputProjectLongTermPlans(
            projectOwnership = projectLongTermPlans.projectOwnership,
            projectDurability = projectLongTermPlans.projectDurability,
            projectTransferability = projectLongTermPlans.projectTransferability
        )

    }

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
    fun getProjectDescription() {
        every { projectOverallObjectiveRepository.findFirstByProjectId(eq(1)) } returns ProjectOverallObjective(1, "overall-val")
        every { projectRelevanceRepository.findFirstByProjectId(eq(1)) } returns projectRelevanceEntity
        every { projectPartnershipRepository.findFirstByProjectId(eq(1)) } returns ProjectPartnership(1, "partner-val")
        every { projectManagementRepository.findFirstByProjectId(eq(1)) } returns projectManagementEntity
        every { projectLongTermPlansRepository.findFirstByProjectId(eq(1)) } returns projectLongTermPlansEntity

        assertThat(projectDescriptionService.getProjectDescription(1))
            .isEqualTo(
                OutputProjectDescription(
                    projectOverallObjective = "overall-val",
                    projectRelevance = projectRelevance,
                    projectPartnership = "partner-val",
                    projectManagement = outputProjectManagement,
                    projectLongTermPlans = outputProjectLongTermPlans
                )
            )
    }

    @Test
    fun updateOverallObjective() {
        every { projectOverallObjectiveRepository.save(any<ProjectOverallObjective>()) } returnsArgument 0

        var testInput = InputProjectOverallObjective("test `val`ue")
        assertThat(projectDescriptionService.updateOverallObjective(1L, testInput))
            .isEqualTo(testInput.copy())

        testInput = InputProjectOverallObjective(null)
        assertThat(projectDescriptionService.updateOverallObjective(2L, testInput))
            .isEqualTo(testInput.copy())
    }

    @Test
    fun updateProjectRelevance() {
        every { projectRelevanceRepository.save(any<ProjectRelevance>()) } returnsArgument 0

        assertThat(projectDescriptionService.updateProjectRelevance(1L, projectRelevance))
            .overridingErrorMessage("value should be saved like it was specified")
            .isEqualTo(projectRelevance.copy())
    }

    @Test
    fun updatePartnership() {
        every { projectPartnershipRepository.save(any<ProjectPartnership>()) } returnsArgument 0

        var testInput = InputProjectPartnership("test value")
        assertThat(projectDescriptionService.updatePartnership(1L, testInput))
            .isEqualTo(testInput.copy())

        testInput = InputProjectPartnership(null)
        assertThat(projectDescriptionService.updatePartnership(2L, testInput))
            .isEqualTo(testInput.copy())
    }

    @Test
    fun updateProjectManagement() {
        every { projectManagementRepository.save(any<ProjectManagement>()) } returnsArgument 0

        assertThat(projectDescriptionService.updateProjectManagement(1L, projectManagement))
            .overridingErrorMessage("value should be saved like it was specified")
            .isEqualTo(outputProjectManagement)
    }

    @Test
    fun `updateProjectManagement empty horizontal and cooperation`() {
        every { projectManagementRepository.save(any<ProjectManagement>()) } returnsArgument 0

        assertThat(projectDescriptionService.updateProjectManagement(2L,
            projectManagement.copy(
                projectCooperationCriteria = null,
                projectHorizontalPrinciples = null
            )
        ))
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

        assertThat(projectDescriptionService.updateProjectLongTermPlans(1L, projectLongTermPlans))
            .overridingErrorMessage("value should be saved like it was specified")
            .isEqualTo(outputProjectLongTermPlans)
    }

}
