package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.programme.dto.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.InputProjectCooperationCriteria
import io.cloudflight.jems.api.project.dto.description.InputProjectHorizontalPrinciples
import io.cloudflight.jems.api.project.dto.description.InputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.InputProjectManagement
import io.cloudflight.jems.api.project.dto.description.InputProjectOverallObjective
import io.cloudflight.jems.api.project.dto.description.InputProjectPartnership
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevance
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevanceBenefit
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevanceStrategy
import io.cloudflight.jems.api.project.dto.description.InputProjectRelevanceSynergy
import io.cloudflight.jems.api.project.dto.description.OutputProjectDescription
import io.cloudflight.jems.api.project.dto.description.OutputProjectLongTermPlans
import io.cloudflight.jems.api.project.dto.description.OutputProjectManagement
import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.server.project.entity.description.ProjectCooperationCriteria
import io.cloudflight.jems.server.project.entity.description.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.entity.description.ProjectLongTermPlans
import io.cloudflight.jems.server.project.entity.description.ProjectManagement
import io.cloudflight.jems.server.project.entity.description.ProjectOverallObjective
import io.cloudflight.jems.server.project.entity.description.ProjectPartnership
import io.cloudflight.jems.server.project.entity.description.ProjectRelevance
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceStrategy
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSynergy
import io.cloudflight.jems.server.project.repository.description.ProjectLongTermPlansRepository
import io.cloudflight.jems.server.project.repository.description.ProjectManagementRepository
import io.cloudflight.jems.server.project.repository.description.ProjectOverallObjectiveRepository
import io.cloudflight.jems.server.project.repository.description.ProjectPartnershipRepository
import io.cloudflight.jems.server.project.repository.description.ProjectRelevanceRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class ProjectDescriptionServiceTest {
    companion object {
        private val projectRelevance = InputProjectRelevance(
            territorialChallenge = setOf(
                InputTranslation(SystemLanguage.DE, "ein andrer Wert"),
                InputTranslation(SystemLanguage.EN, "territorial value")
            ),
            commonChallenge = setOf(
                InputTranslation(SystemLanguage.DE, null),
                InputTranslation(SystemLanguage.EN, "common value")
            ),
            transnationalCooperation = setOf(
                InputTranslation(SystemLanguage.DE, null),
                InputTranslation(SystemLanguage.EN, "transnational value")
            ),
            projectBenefits = listOf(
                InputProjectRelevanceBenefit(
                    group = ProjectTargetGroup.Egtc,
                    specification = setOf(
                        InputTranslation(SystemLanguage.DE, null),
                        InputTranslation(SystemLanguage.EN, "test Egtc")
                    )
                )
            ),
            projectStrategies = listOf(
                InputProjectRelevanceStrategy(
                    strategy = ProgrammeStrategy.AtlanticStrategy,
                    specification = setOf(
                        InputTranslation(SystemLanguage.DE, null),
                        InputTranslation(SystemLanguage.EN, "test AtlanticStrategy")
                    )
                )
            ),
            projectSynergies = listOf(
                InputProjectRelevanceSynergy(
                    synergy = setOf(
                        InputTranslation(SystemLanguage.DE, null),
                        InputTranslation(SystemLanguage.EN, "syn 01")
                    ),
                    specification = setOf(
                        InputTranslation(SystemLanguage.DE, null),
                        InputTranslation(SystemLanguage.EN, "test syn 01")
                    )
                )
            ),
            availableKnowledge = setOf(
                InputTranslation(SystemLanguage.DE, null),
                InputTranslation(SystemLanguage.EN, "available knowledge value")
            )
        )

        private val projectBenefitUuid = UUID.randomUUID()
        private val projectStrategyUuid = UUID.randomUUID()
        private val projectSynergyUuid = UUID.randomUUID()
        private val projectRelevanceEntity = ProjectRelevance(
            projectId = 1,
            translatedValues = combineTranslatedValuesRelevance(
                1,
                projectRelevance.territorialChallenge,
                projectRelevance.commonChallenge,
                projectRelevance.transnationalCooperation,
                projectRelevance.availableKnowledge
            ),
            projectBenefits = setOf(
                ProjectRelevanceBenefit(
                    id = projectBenefitUuid,
                    targetGroup = ProjectTargetGroup.Egtc,
                    translatedValues = combineTranslatedValuesBenefit(
                        projectBenefitUuid,
                        projectRelevance.projectBenefits?.get(0)!!.specification
                    )
                )
            ),
            projectStrategies = setOf(
                ProjectRelevanceStrategy(
                    id = projectStrategyUuid,
                    strategy = ProgrammeStrategy.AtlanticStrategy,
                    translatedValues = combineTranslatedValuesStrategy(
                        projectStrategyUuid,
                        projectRelevance.projectStrategies?.get(0)!!.specification
                    )
                )
            ),
            projectSynergies = setOf(
                ProjectRelevanceSynergy(
                    id = projectSynergyUuid,
                    translatedValues = combineTranslatedValuesSynergy(
                        projectSynergyUuid,
                        projectRelevance.projectSynergies?.get(0)!!.synergy,
                        projectRelevance.projectSynergies?.get(0)!!.specification
                    )
                )
            )
        )

        private val cooperationCriteria = InputProjectCooperationCriteria(
            projectJointDevelopment = true,
            projectJointImplementation = false,
            projectJointStaffing = true,
            projectJointFinancing = false,
        )

        private val cooperationCriteriaEntity = ProjectCooperationCriteria(
            projectJointDevelopment = cooperationCriteria.projectJointDevelopment,
            projectJointImplementation = cooperationCriteria.projectJointImplementation,
            projectJointStaffing = cooperationCriteria.projectJointStaffing,
            projectJointFinancing = cooperationCriteria.projectJointFinancing,
        )

        private val horizontalPrinciples = InputProjectHorizontalPrinciples(
            sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.Neutral,
            equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
            sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects,
        )

        private val horizontalPrinciplesEntity = ProjectHorizontalPrinciples(
            sustainableDevelopmentCriteriaEffect = horizontalPrinciples.sustainableDevelopmentCriteriaEffect,
            equalOpportunitiesEffect = horizontalPrinciples.equalOpportunitiesEffect,
            sexualEqualityEffect = horizontalPrinciples.sexualEqualityEffect,
        )

        private val projectManagement = InputProjectManagement(
            projectCoordination = setOf(InputTranslation(SystemLanguage.EN, "coordination value")),
            projectQualityAssurance = setOf(InputTranslation(SystemLanguage.EN, "ownership value")),
            projectCommunication = setOf(InputTranslation(SystemLanguage.EN, "communication value")),
            projectFinancialManagement = setOf(InputTranslation(SystemLanguage.EN, "financial value")),
            projectCooperationCriteria = cooperationCriteria,
            projectJointDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointDevelopmentDescription")),
            projectJointImplementationDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointImplementationDescription")),
            projectJointStaffingDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointStaffingDescription")),
            projectJointFinancingDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointFinancingDescription")),
            projectHorizontalPrinciples = horizontalPrinciples,
            sustainableDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "sustainableDevelopmentDescription")),
            equalOpportunitiesDescription = setOf(InputTranslation(SystemLanguage.EN, "equalOpportunitiesDescription")),
            sexualEqualityDescription = setOf(InputTranslation(SystemLanguage.EN, "sexualEqualityDescription"))
        )

        private val projectManagementEntity = ProjectManagement(
            projectId = 1,
            translatedValues = combineTranslatedValuesManagement(
                1,
                projectManagement.projectCoordination,
                projectManagement.projectQualityAssurance,
                projectManagement.projectCommunication,
                projectManagement.projectFinancialManagement,
                projectManagement.projectJointDevelopmentDescription,
                projectManagement.projectJointImplementationDescription,
                projectManagement.projectJointStaffingDescription,
                projectManagement.projectJointFinancingDescription,
                projectManagement.sustainableDevelopmentDescription,
                projectManagement.equalOpportunitiesDescription,
                projectManagement.sexualEqualityDescription
            ),
            projectCooperationCriteria = cooperationCriteriaEntity,
            projectHorizontalPrinciples = horizontalPrinciplesEntity
        )

        private val outputProjectManagement = OutputProjectManagement(
            projectCoordination = projectManagement.projectCoordination,
            projectQualityAssurance = projectManagement.projectQualityAssurance,
            projectCommunication = projectManagement.projectCommunication,
            projectFinancialManagement = projectManagement.projectFinancialManagement,
            projectCooperationCriteria = cooperationCriteria.copy(),
            projectJointDevelopmentDescription = projectManagement.projectJointDevelopmentDescription,
            projectJointImplementationDescription = projectManagement.projectJointImplementationDescription,
            projectJointStaffingDescription = projectManagement.projectJointStaffingDescription,
            projectJointFinancingDescription = projectManagement.projectJointFinancingDescription,
            projectHorizontalPrinciples = horizontalPrinciples.copy(),
            sustainableDevelopmentDescription = projectManagement.sustainableDevelopmentDescription,
            equalOpportunitiesDescription = projectManagement.equalOpportunitiesDescription,
            sexualEqualityDescription = projectManagement.sexualEqualityDescription
        )

        private val projectLongTermPlans = InputProjectLongTermPlans(
            projectOwnership = setOf(InputTranslation(SystemLanguage.EN, "ownership value")),
            projectDurability = setOf(InputTranslation(SystemLanguage.EN, "durability")),
            projectTransferability = setOf(InputTranslation(SystemLanguage.EN, "transferability"))
        )

        private val projectLongTermPlansEntity = ProjectLongTermPlans(
            projectId = 1,
            translatedValues = combineTranslatedValuesLongTermPlans(
                1,
                projectLongTermPlans.projectOwnership,
                projectLongTermPlans.projectDurability,
                projectLongTermPlans.projectTransferability
            )
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
        val inputProjectOverallObjective =
            InputProjectOverallObjective(setOf(InputTranslation(SystemLanguage.EN, "overall-val")))
        val inputProjectPartnership = InputProjectPartnership(setOf(InputTranslation(SystemLanguage.EN, "partner-val")))
        every { projectOverallObjectiveRepository.findFirstByProjectId(eq(1)) } returns inputProjectOverallObjective.toEntity(
            1
        )
        every { projectRelevanceRepository.findFirstByProjectId(eq(1)) } returns projectRelevanceEntity
        every { projectPartnershipRepository.findFirstByProjectId(eq(1)) } returns inputProjectPartnership.toEntity(1)
        every { projectManagementRepository.findFirstByProjectId(eq(1)) } returns projectManagementEntity
        every { projectLongTermPlansRepository.findFirstByProjectId(eq(1)) } returns projectLongTermPlansEntity

        assertThat(projectDescriptionService.getProjectDescription(1))
            .isEqualTo(
                OutputProjectDescription(
                    projectOverallObjective = inputProjectOverallObjective,
                    projectRelevance = projectRelevance,
                    projectPartnership = inputProjectPartnership,
                    projectManagement = outputProjectManagement,
                    projectLongTermPlans = outputProjectLongTermPlans
                )
            )
    }

    @Test
    fun updateOverallObjective() {
        every { projectOverallObjectiveRepository.save(any<ProjectOverallObjective>()) } returnsArgument 0

        var testInput = InputProjectOverallObjective(setOf(InputTranslation(SystemLanguage.EN, "test `val`ue")))
        assertThat(projectDescriptionService.updateOverallObjective(1L, testInput))
            .isEqualTo(testInput.copy())

        testInput = InputProjectOverallObjective(emptySet())
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

        var testInput = InputProjectPartnership(setOf(InputTranslation(SystemLanguage.EN, "test value")))
        assertThat(projectDescriptionService.updatePartnership(1L, testInput))
            .isEqualTo(testInput.copy())

        testInput = InputProjectPartnership(emptySet())
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

        assertThat(
            projectDescriptionService.updateProjectManagement(
                2L,
                projectManagement.copy(
                    projectCooperationCriteria = null,
                    projectHorizontalPrinciples = null
                )
            )
        )
            .overridingErrorMessage("empty embedded classes should be omitted")
            .isEqualTo(
                OutputProjectManagement(
                    projectCoordination = projectManagement.projectCoordination,
                    projectQualityAssurance = projectManagement.projectQualityAssurance,
                    projectCommunication = projectManagement.projectCommunication,
                    projectFinancialManagement = projectManagement.projectFinancialManagement,
                    projectJointDevelopmentDescription = projectManagement.projectJointDevelopmentDescription,
                    projectJointImplementationDescription = projectManagement.projectJointImplementationDescription,
                    projectJointStaffingDescription = projectManagement.projectJointStaffingDescription,
                    projectJointFinancingDescription = projectManagement.projectJointFinancingDescription,
                    sustainableDevelopmentDescription = projectManagement.sustainableDevelopmentDescription,
                    equalOpportunitiesDescription = projectManagement.equalOpportunitiesDescription,
                    sexualEqualityDescription = projectManagement.sexualEqualityDescription,
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
