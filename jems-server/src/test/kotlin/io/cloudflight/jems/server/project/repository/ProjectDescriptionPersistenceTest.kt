package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.description.ProjectCooperationCriteriaEntity
import io.cloudflight.jems.server.project.entity.description.ProjectHorizontalPrinciplesEntity
import io.cloudflight.jems.server.project.entity.description.ProjectLongTermPlansEntity
import io.cloudflight.jems.server.project.entity.description.ProjectLongTermPlansRow
import io.cloudflight.jems.server.project.entity.description.ProjectManagementEntity
import io.cloudflight.jems.server.project.entity.description.ProjectManagementRow
import io.cloudflight.jems.server.project.entity.description.ProjectOverallObjectiveEntity
import io.cloudflight.jems.server.project.entity.description.ProjectOverallObjectiveRow
import io.cloudflight.jems.server.project.entity.description.ProjectPartnershipEntity
import io.cloudflight.jems.server.project.entity.description.ProjectPartnershipRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceBenefitEntity
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceBenefitRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceEntity
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceStrategyEntity
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceStrategyRow
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSynergyEntity
import io.cloudflight.jems.server.project.entity.description.ProjectRelevanceSynergyRow
import io.cloudflight.jems.server.project.repository.description.ProjectLongTermPlansRepository
import io.cloudflight.jems.server.project.repository.description.ProjectManagementRepository
import io.cloudflight.jems.server.project.repository.description.ProjectOverallObjectiveRepository
import io.cloudflight.jems.server.project.repository.description.ProjectPartnershipRepository
import io.cloudflight.jems.server.project.repository.description.ProjectRelevanceRepository
import io.cloudflight.jems.server.project.service.combineTranslatedValuesBenefit
import io.cloudflight.jems.server.project.service.combineTranslatedValuesLongTermPlans
import io.cloudflight.jems.server.project.service.combineTranslatedValuesManagement
import io.cloudflight.jems.server.project.service.combineTranslatedValuesOverallObjective
import io.cloudflight.jems.server.project.service.combineTranslatedValuesPartnership
import io.cloudflight.jems.server.project.service.combineTranslatedValuesRelevance
import io.cloudflight.jems.server.project.service.combineTranslatedValuesStrategy
import io.cloudflight.jems.server.project.service.combineTranslatedValuesSynergy
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
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

/**
 * tests implementation of ProjectDescriptionPersistenceProvider including mappings and projectVersionUtils
 */
internal class ProjectDescriptionPersistenceTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private val projectBenefitUuid = UUID.randomUUID()
        private val projectStrategyUuid = UUID.randomUUID()
        private val projectSynergyUuid = UUID.randomUUID()

        // test data of ProjectDescription Entities
        private fun dummyProjectOverallObjective(): ProjectOverallObjectiveEntity =
            ProjectOverallObjectiveEntity(
                projectId = PROJECT_ID,
                translatedValues = combineTranslatedValuesOverallObjective(
                    projectId = PROJECT_ID,
                    overallObjective = setOf(InputTranslation(SystemLanguage.EN, "overallObjective"))
                )
            )
        private fun dummyProjectRelevance() = ProjectRelevanceEntity(
            projectId = PROJECT_ID,
            translatedValues = combineTranslatedValuesRelevance(
                projectId = PROJECT_ID,
                territorialChallenge = setOf(InputTranslation(SystemLanguage.EN, "territorialChallenge")),
                commonChallenge = setOf(InputTranslation(SystemLanguage.EN, "commonChallenge")),
                transnationalCooperation = setOf(InputTranslation(SystemLanguage.EN, "transnationalCooperation")),
                availableKnowledge = setOf(InputTranslation(SystemLanguage.EN, "availableKnowledge"))
            ),
            projectBenefits = setOf(
                ProjectRelevanceBenefitEntity(
                    id = projectBenefitUuid,
                    targetGroup = ProjectTargetGroupDTO.LocalPublicAuthority,
                    translatedValues = combineTranslatedValuesBenefit(
                        uuid = projectBenefitUuid,
                        specification = setOf(InputTranslation(SystemLanguage.EN, "specification"))
                    )
                )
            ),
            projectStrategies = setOf(
                ProjectRelevanceStrategyEntity(
                    id = projectStrategyUuid,
                    strategy = ProgrammeStrategy.AtlanticStrategy,
                    translatedValues = combineTranslatedValuesStrategy(
                        uuid = projectStrategyUuid,
                        specification = setOf(InputTranslation(SystemLanguage.EN, "specification"))
                    )
                )
            ),
            projectSynergies = setOf(
                ProjectRelevanceSynergyEntity(
                    id = projectSynergyUuid,
                    translatedValues = combineTranslatedValuesSynergy(
                        uuid = projectSynergyUuid,
                        synergy = setOf(InputTranslation(SystemLanguage.EN, "synergy")),
                        specification = setOf(InputTranslation(SystemLanguage.EN, "specification"))
                    )
                )
            )
        )
        private fun dummyProjectPartnership() = ProjectPartnershipEntity(
            projectId = PROJECT_ID,
            translatedValues = combineTranslatedValuesPartnership(
                projectId = PROJECT_ID,
                partnership = setOf(InputTranslation(SystemLanguage.EN, "partnership"))
            )
        )
        private fun dummyProjectManagement() = ProjectManagementEntity(
            projectId = PROJECT_ID,
            projectCooperationCriteria = ProjectCooperationCriteriaEntity(
                projectJointDevelopment = true,
                projectJointImplementation = true,
                projectJointStaffing = true,
                projectJointFinancing = true
            ),
            projectHorizontalPrinciples = ProjectHorizontalPrinciplesEntity(
                sustainableDevelopmentCriteriaEffect = ProjectHorizontalPrinciplesEffect.PositiveEffects,
                equalOpportunitiesEffect = ProjectHorizontalPrinciplesEffect.Neutral,
                sexualEqualityEffect = ProjectHorizontalPrinciplesEffect.NegativeEffects
            ),
            translatedValues = combineTranslatedValuesManagement(
                projectId = PROJECT_ID,
                projectCoordination = setOf(InputTranslation(SystemLanguage.EN, "projectCoordination")),
                projectQualityAssurance = setOf(InputTranslation(SystemLanguage.EN, "projectQualityAssurance")),
                projectCommunication = setOf(InputTranslation(SystemLanguage.EN, "projectCommunication")),
                projectFinancialManagement = setOf(InputTranslation(SystemLanguage.EN, "projectFinancialManagement")),
                projectJointDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointDevelopmentDescription")),
                projectJointImplementationDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointImplementationDescription")),
                projectJointStaffingDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointStaffingDescription")),
                projectJointFinancingDescription = setOf(InputTranslation(SystemLanguage.EN, "projectJointFinancingDescription")),
                sustainableDevelopmentDescription = setOf(InputTranslation(SystemLanguage.EN, "sustainableDevelopmentDescription")),
                equalOpportunitiesDescription = setOf(InputTranslation(SystemLanguage.EN, "equalOpportunitiesDescription")),
                sexualEqualityDescription = setOf(InputTranslation(SystemLanguage.EN, "sexualEqualityDescription"))
            )
        )
        private fun dummyProjectLongTermPlans() = ProjectLongTermPlansEntity(
            projectId = PROJECT_ID,
            translatedValues = combineTranslatedValuesLongTermPlans(
                projectId = PROJECT_ID,
                projectOwnership = setOf(InputTranslation(SystemLanguage.EN, "projectOwnership")),
                projectTransferability = setOf(InputTranslation(SystemLanguage.EN, "projectTransferability")),
                projectDurability = setOf(InputTranslation(SystemLanguage.EN, "projectDurability"))
            )
        )

        // model for ProjectDescription
        private fun modelProjectDescription() = ProjectDescription(
            projectOverallObjective = ProjectOverallObjective(
                overallObjective = setOf(InputTranslation(SystemLanguage.EN, "overallObjective"))
            ),
            projectRelevance = ProjectRelevance(
                territorialChallenge = setOf(InputTranslation(SystemLanguage.EN, "territorialChallenge")),
                commonChallenge = setOf(InputTranslation(SystemLanguage.EN, "commonChallenge")),
                transnationalCooperation = setOf(InputTranslation(SystemLanguage.EN, "transnationalCooperation")),
                projectBenefits = listOf(ProjectRelevanceBenefit(
                    group = ProjectTargetGroupDTO.LocalPublicAuthority,
                    specification = setOf(InputTranslation(SystemLanguage.EN, "specification"))
                )),
                projectStrategies = listOf(ProjectRelevanceStrategy(
                    strategy = ProgrammeStrategy.AtlanticStrategy,
                    specification = setOf(InputTranslation(SystemLanguage.EN, "specification"))
                )),
                projectSynergies = listOf(ProjectRelevanceSynergy(
                    synergy = setOf(InputTranslation(SystemLanguage.EN, "synergy")),
                    specification = setOf(InputTranslation(SystemLanguage.EN, "specification"))
                )),
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
                    projectJointImplementation = true,
                    projectJointStaffing = true,
                    projectJointFinancing = true
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
    }

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    private lateinit var projectVersionUtils: ProjectVersionUtils

    @RelaxedMockK
    lateinit var projectOverallObjectiveRepository: ProjectOverallObjectiveRepository
    @RelaxedMockK
    lateinit var projectRelevanceRepository: ProjectRelevanceRepository
    @RelaxedMockK
    lateinit var projectPartnershipRepository: ProjectPartnershipRepository
    @RelaxedMockK
    lateinit var projectManagementRepository: ProjectManagementRepository
    @RelaxedMockK
    lateinit var projectLongTermPlansRepository: ProjectLongTermPlansRepository

    private lateinit var persistence: ProjectDescriptionPersistenceProvider

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = ProjectDescriptionPersistenceProvider(projectVersionUtils, projectOverallObjectiveRepository, projectRelevanceRepository, projectPartnershipRepository, projectManagementRepository, projectLongTermPlansRepository)
    }

    @Test
    fun `get ProjectDescription - everything OK`() {
        val overallObjective = dummyProjectOverallObjective()
        val relevance = dummyProjectRelevance()
        val partnership = dummyProjectPartnership()
        val management = dummyProjectManagement()
        val longTermPlans = dummyProjectLongTermPlans()
        every { projectOverallObjectiveRepository.findFirstByProjectId(PROJECT_ID) } returns overallObjective
        every { projectRelevanceRepository.findFirstByProjectId(PROJECT_ID) } returns relevance
        every { projectPartnershipRepository.findFirstByProjectId(PROJECT_ID) } returns partnership
        every { projectManagementRepository.findFirstByProjectId(PROJECT_ID) } returns management
        every { projectLongTermPlansRepository.findFirstByProjectId(PROJECT_ID) } returns longTermPlans

        assertThat(persistence.getProjectDescription(PROJECT_ID))
            .isEqualTo(modelProjectDescription())
    }

    @Test
    fun `get ProjectDescription with previous version`() {
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val version = "3.0"
        val mockOORow: ProjectOverallObjectiveRow = mockk()
        every { mockOORow.projectId } returns PROJECT_ID
        every { mockOORow.language } returns SystemLanguage.EN
        every { mockOORow.overallObjective } returns "overallObjective"

        val mockRRow: ProjectRelevanceRow = mockk()
        every { mockRRow.projectId } returns PROJECT_ID
        every { mockRRow.language } returns SystemLanguage.EN
        every { mockRRow.territorialChallenge } returns "territorialChallenge"
        every { mockRRow.commonChallenge } returns "commonChallenge"
        every { mockRRow.transnationalCooperation } returns "transnationalCooperation"
        every { mockRRow.availableKnowledge } returns "availableKnowledge"
        val mockRBeRow: ProjectRelevanceBenefitRow = mockk()
        every { mockRBeRow.id } returns projectBenefitUuid.toString()
        every { mockRBeRow.projectId } returns PROJECT_ID
        every { mockRBeRow.language } returns SystemLanguage.EN
        every { mockRBeRow.targetGroup } returns ProjectTargetGroupDTO.LocalPublicAuthority
        every { mockRBeRow.specification } returns "specification"
        val mockRStRow: ProjectRelevanceStrategyRow = mockk()
        every { mockRStRow.id } returns projectStrategyUuid.toString()
        every { mockRStRow.projectId } returns PROJECT_ID
        every { mockRStRow.language } returns SystemLanguage.EN
        every { mockRStRow.strategy } returns ProgrammeStrategy.AtlanticStrategy
        every { mockRStRow.specification } returns "specification"
        val mockRSyRow: ProjectRelevanceSynergyRow = mockk()
        every { mockRSyRow.id } returns projectSynergyUuid.toString()
        every { mockRSyRow.projectId } returns PROJECT_ID
        every { mockRSyRow.language } returns SystemLanguage.EN
        every { mockRSyRow.synergy } returns "synergy"
        every { mockRSyRow.specification } returns "specification"

        val mockPRow: ProjectPartnershipRow = mockk()
        every { mockPRow.projectId } returns PROJECT_ID
        every { mockPRow.language } returns SystemLanguage.EN
        every { mockPRow.projectPartnership } returns "partnership"
        val mockMRow: ProjectManagementRow = mockk()
        every { mockMRow.projectId } returns PROJECT_ID
        every { mockMRow.language } returns SystemLanguage.EN
        every { mockMRow.projectJointDevelopment } returns true
        every { mockMRow.projectJointImplementation } returns true
        every { mockMRow.projectJointStaffing } returns true
        every { mockMRow.projectJointFinancing } returns true
        every { mockMRow.sustainableDevelopmentCriteriaEffect } returns ProjectHorizontalPrinciplesEffect.PositiveEffects
        every { mockMRow.equalOpportunitiesEffect } returns ProjectHorizontalPrinciplesEffect.Neutral
        every { mockMRow.sexualEqualityEffect } returns ProjectHorizontalPrinciplesEffect.NegativeEffects
        every { mockMRow.projectCoordination } returns "projectCoordination"
        every { mockMRow.projectQualityAssurance } returns "projectQualityAssurance"
        every { mockMRow.projectCommunication } returns "projectCommunication"
        every { mockMRow.projectFinancialManagement } returns "projectFinancialManagement"
        every { mockMRow.projectJointDevelopmentDescription } returns "projectJointDevelopmentDescription"
        every { mockMRow.projectJointImplementationDescription } returns "projectJointImplementationDescription"
        every { mockMRow.projectJointStaffingDescription } returns "projectJointStaffingDescription"
        every { mockMRow.projectJointFinancingDescription } returns "projectJointFinancingDescription"
        every { mockMRow.sustainableDevelopmentDescription } returns "sustainableDevelopmentDescription"
        every { mockMRow.equalOpportunitiesDescription } returns "equalOpportunitiesDescription"
        every { mockMRow.sexualEqualityDescription } returns "sexualEqualityDescription"
        val mockLTPRow: ProjectLongTermPlansRow = mockk()
        every { mockLTPRow.projectId } returns PROJECT_ID
        every { mockLTPRow.language } returns SystemLanguage.EN
        every { mockLTPRow.projectOwnership } returns "projectOwnership"
        every { mockLTPRow.projectDurability } returns "projectDurability"
        every { mockLTPRow.projectTransferability } returns "projectTransferability"

        every { projectVersionRepo.findTimestampByVersion(PROJECT_ID, version) } returns timestamp
        every { projectOverallObjectiveRepository.findByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns listOf(mockOORow)
        every { projectRelevanceRepository.findByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns listOf(mockRRow)
        every { projectRelevanceRepository.findBenefitsByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns listOf(mockRBeRow)
        every { projectRelevanceRepository.findStrategiesByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns listOf(mockRStRow)
        every { projectRelevanceRepository.findSynergiesByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns listOf(mockRSyRow)
        every { projectPartnershipRepository.findByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns listOf(mockPRow)
        every { projectManagementRepository.findByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns listOf(mockMRow)
        every { projectLongTermPlansRepository.findByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns listOf(mockLTPRow)

        assertThat(persistence.getProjectDescription(PROJECT_ID, version))
            .isEqualTo(modelProjectDescription())
    }

    @Test
    fun `get ProjectDescription for previous version with empty values`() {
        val emptyProjectDescription = ProjectDescription(
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
                    projectJointImplementation = false,
                    projectJointStaffing = false,
                    projectJointFinancing = false
                ),
                projectJointDevelopmentDescription = emptySet(),
                projectJointImplementationDescription = emptySet(),
                projectJointStaffingDescription = emptySet(),
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
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val version = "4.0"

        every { projectVersionRepo.findTimestampByVersion(PROJECT_ID, version) } returns timestamp
        every { projectOverallObjectiveRepository.findByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns emptyList()
        every { projectRelevanceRepository.findByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns emptyList()
        every { projectRelevanceRepository.findBenefitsByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns emptyList()
        every { projectRelevanceRepository.findStrategiesByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns emptyList()
        every { projectRelevanceRepository.findSynergiesByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns emptyList()
        every { projectPartnershipRepository.findByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns emptyList()
        every { projectManagementRepository.findByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns emptyList()
        every { projectLongTermPlansRepository.findByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns emptyList()

        assertThat(persistence.getProjectDescription(PROJECT_ID, version))
            .isEqualTo(emptyProjectDescription)
    }

    @Test
    fun `update ProjectDescription OverallObjective`() {
        val projectOverallObjective = modelProjectDescription().projectOverallObjective!!
        val overallObjectiveEntity = dummyProjectOverallObjective()
        every { projectOverallObjectiveRepository.save(overallObjectiveEntity) } returns overallObjectiveEntity

        assertThat(persistence.updateOverallObjective(PROJECT_ID, projectOverallObjective)).isEqualTo(projectOverallObjective)
    }

    @Test
    fun `update ProjectDescription ProjectRelevance`() {
        val projectRelevance = modelProjectDescription().projectRelevance!!
        val projectRelevanceEntity = dummyProjectRelevance()
        every { projectRelevanceRepository.save(any()) } returns projectRelevanceEntity

        assertThat(persistence.updateProjectRelevance(PROJECT_ID, projectRelevance)).isEqualTo(projectRelevance)
    }

    @Test
    fun `update ProjectDescription Partnership`() {
        val projectPartnership = modelProjectDescription().projectPartnership!!
        val partnershipEntity = dummyProjectPartnership()
        every { projectPartnershipRepository.save(partnershipEntity) } returns partnershipEntity

        assertThat(persistence.updatePartnership(PROJECT_ID, projectPartnership)).isEqualTo(projectPartnership)
    }

    @Test
    fun `update ProjectDescription Management`() {
        val projectManagement = modelProjectDescription().projectManagement!!
        val managementEntity = dummyProjectManagement()
        every { projectManagementRepository.save(managementEntity) } returns managementEntity

        assertThat(persistence.updateProjectManagement(PROJECT_ID, projectManagement)).isEqualTo(projectManagement)
    }

    @Test
    fun `update ProjectDescription LongTermPlans`() {
        val projectLongTermPlans = modelProjectDescription().projectLongTermPlans!!
        val longTermPlansEntity = dummyProjectLongTermPlans()
        every { projectLongTermPlansRepository.save(longTermPlansEntity) } returns longTermPlansEntity

        assertThat(persistence.updateProjectLongTermPlans(PROJECT_ID, projectLongTermPlans)).isEqualTo(projectLongTermPlans)
    }
}
