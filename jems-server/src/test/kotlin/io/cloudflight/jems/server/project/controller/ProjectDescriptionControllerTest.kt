package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
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
import io.cloudflight.jems.server.project.service.get_project_description.GetProjectDescriptionInteractor
import io.cloudflight.jems.server.project.service.get_project_versions.GetProjectVersionsInteractor
import io.cloudflight.jems.server.project.service.model.ProjectCooperationCriteria
import io.cloudflight.jems.server.project.service.model.ProjectDescription
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectLongTermPlans
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectOverallObjective
import io.cloudflight.jems.server.project.service.model.ProjectPartnership
import io.cloudflight.jems.server.project.service.model.ProjectRelevance
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceStrategy
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceSynergy
import io.cloudflight.jems.server.project.service.update_project_description.UpdateProjectDescriptionInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProjectDescriptionControllerTest {

    companion object {
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
                    group = ProjectTargetGroup.LocalPublicAuthority,
                    specification = setOf(InputTranslation(SystemLanguage.EN, "specification")))
                ),
                projectStrategies = listOf(
                    ProjectRelevanceStrategy(
                    strategy = ProgrammeStrategy.AtlanticStrategy,
                    specification = setOf(InputTranslation(SystemLanguage.EN, "specification")))
                ),
                projectSynergies = listOf(
                    ProjectRelevanceSynergy(
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

        private val outputProjectDescription = OutputProjectDescription(
            projectOverallObjective = InputProjectOverallObjective(
                overallObjective = setOf(InputTranslation(SystemLanguage.EN, "overallObjective"))
            ),
            projectRelevance = InputProjectRelevance(
                territorialChallenge = setOf(InputTranslation(SystemLanguage.EN, "territorialChallenge")),
                commonChallenge = setOf(InputTranslation(SystemLanguage.EN, "commonChallenge")),
                transnationalCooperation = setOf(InputTranslation(SystemLanguage.EN, "transnationalCooperation")),
                projectBenefits = listOf(
                    InputProjectRelevanceBenefit(
                        group = ProjectTargetGroup.LocalPublicAuthority,
                        specification = setOf(InputTranslation(SystemLanguage.EN, "specification")))
                ),
                projectStrategies = listOf(
                    InputProjectRelevanceStrategy(
                        strategy = ProgrammeStrategy.AtlanticStrategy,
                        specification = setOf(InputTranslation(SystemLanguage.EN, "specification")))
                ),
                projectSynergies = listOf(
                    InputProjectRelevanceSynergy(
                        synergy = setOf(InputTranslation(SystemLanguage.EN, "synergy")),
                        specification = setOf(InputTranslation(SystemLanguage.EN, "specification")))
                ),
                availableKnowledge = setOf(InputTranslation(SystemLanguage.EN, "availableKnowledge"))
            ),
            projectPartnership = InputProjectPartnership(
                partnership = setOf(InputTranslation(SystemLanguage.EN, "partnership"))
            ),
            projectManagement = OutputProjectManagement(
                projectCoordination = setOf(InputTranslation(SystemLanguage.EN, "projectCoordination")),
                projectQualityAssurance = setOf(InputTranslation(SystemLanguage.EN, "projectQualityAssurance")),
                projectCommunication = setOf(InputTranslation(SystemLanguage.EN, "projectCommunication")),
                projectFinancialManagement = setOf(InputTranslation(SystemLanguage.EN, "projectFinancialManagement")),
                projectCooperationCriteria = InputProjectCooperationCriteria(
                    projectJointDevelopment = true,
                    projectJointFinancing = true,
                    projectJointImplementation = true,
                    projectJointStaffing = true
                ),
                projectJointDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointDevelopmentDescription")),
                projectJointImplementationDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointImplementationDescription")),
                projectJointStaffingDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointStaffingDescription")),
                projectJointFinancingDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointFinancingDescription")),
                projectHorizontalPrinciples = InputProjectHorizontalPrinciples(
                    sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
                    equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.Neutral,
                    sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects
                ),
                sustainableDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "sustainableDevelopmentDescription")),
                equalOpportunitiesDescription = setOf(InputTranslation(SystemLanguage.EN, "equalOpportunitiesDescription")),
                sexualEqualityDescription = setOf(InputTranslation(SystemLanguage.EN, "sexualEqualityDescription"))
            ),
            projectLongTermPlans = OutputProjectLongTermPlans(
                projectOwnership = setOf(InputTranslation(SystemLanguage.EN, "projectOwnership")),
                projectDurability = setOf(InputTranslation(SystemLanguage.EN, "projectDurability")),
                projectTransferability = setOf(InputTranslation(SystemLanguage.EN, "projectTransferability"))
            )
        )

        private val projectManagement = InputProjectManagement(
            projectCoordination = setOf(InputTranslation(SystemLanguage.EN, "projectCoordination")),
            projectQualityAssurance = setOf(InputTranslation(SystemLanguage.EN, "projectQualityAssurance")),
            projectCommunication = setOf(InputTranslation(SystemLanguage.EN, "projectCommunication")),
            projectFinancialManagement = setOf(InputTranslation(SystemLanguage.EN, "projectFinancialManagement")),
            projectCooperationCriteria = InputProjectCooperationCriteria(
                projectJointDevelopment = true,
                projectJointFinancing = true,
                projectJointImplementation = true,
                projectJointStaffing = true
            ),
            projectJointDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointDevelopmentDescription")),
            projectJointImplementationDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointImplementationDescription")),
            projectJointStaffingDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointStaffingDescription")),
            projectJointFinancingDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointFinancingDescription")),
            projectHorizontalPrinciples = InputProjectHorizontalPrinciples(
                sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
                equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.Neutral,
                sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects
            ),
            sustainableDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "sustainableDevelopmentDescription")),
            equalOpportunitiesDescription = setOf(InputTranslation(SystemLanguage.EN, "equalOpportunitiesDescription")),
            sexualEqualityDescription = setOf(InputTranslation(SystemLanguage.EN, "sexualEqualityDescription"))
        )
        private val projectLongTermPlans = InputProjectLongTermPlans(
            projectOwnership = setOf(InputTranslation(SystemLanguage.EN, "projectOwnership")),
            projectDurability = setOf(InputTranslation(SystemLanguage.EN, "projectDurability")),
            projectTransferability = setOf(InputTranslation(SystemLanguage.EN, "projectTransferability"))
        )
    }

    @MockK
    lateinit var updateProjectDescriptionInteractor: UpdateProjectDescriptionInteractor
    @MockK
    lateinit var getProjectDescriptionInteractor: GetProjectDescriptionInteractor

    @InjectMockKs
    private lateinit var controller: ProjectDescriptionController

    @Test
    fun getProjectDescription() {
        every { getProjectDescriptionInteractor.getProjectDescription(1L, "1.0") } returns projectDescription
        assertThat(controller.getProjectDescription(1L, "1.0")).isEqualTo(outputProjectDescription)
    }

    @Test
    fun updateProjectOverallObjective() {
        every { updateProjectDescriptionInteractor.updateOverallObjective(1L, projectDescription.projectOverallObjective!!) } returns projectDescription.projectOverallObjective!!
        assertThat(controller.updateProjectOverallObjective(1L, outputProjectDescription.projectOverallObjective!!))
            .isEqualTo(outputProjectDescription.projectOverallObjective!!)
    }

    @Test
    fun updateProjectRelevance() {
        every { updateProjectDescriptionInteractor.updateProjectRelevance(1L, projectDescription.projectRelevance!!) } returns projectDescription.projectRelevance!!
        assertThat(controller.updateProjectRelevance(1L, outputProjectDescription.projectRelevance!!))
            .isEqualTo(outputProjectDescription.projectRelevance!!)
    }

    @Test
    fun updateProjectPartnership() {
        every { updateProjectDescriptionInteractor.updatePartnership(1L, projectDescription.projectPartnership!!) } returns projectDescription.projectPartnership!!
        assertThat(controller.updateProjectPartnership(1L, outputProjectDescription.projectPartnership!!))
            .isEqualTo(outputProjectDescription.projectPartnership!!)
    }

    @Test
    fun updateProjectManagement() {
        every { updateProjectDescriptionInteractor.updateProjectManagement(1L, projectDescription.projectManagement!!) } returns projectDescription.projectManagement!!
        assertThat(controller.updateProjectManagement(1L, projectManagement))
            .isEqualTo(outputProjectDescription.projectManagement!!)
    }

    @Test
    fun updateProjectLongTermPlans() {
        every { updateProjectDescriptionInteractor.updateProjectLongTermPlans(1L, projectDescription.projectLongTermPlans!!) } returns projectDescription.projectLongTermPlans!!
        assertThat(controller.updateProjectLongTermPlans(1L, projectLongTermPlans))
            .isEqualTo(outputProjectDescription.projectLongTermPlans!!)
    }
}
