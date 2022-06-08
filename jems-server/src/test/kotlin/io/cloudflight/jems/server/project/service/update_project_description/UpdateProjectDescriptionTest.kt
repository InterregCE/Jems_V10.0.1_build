package io.cloudflight.jems.server.project.service.update_project_description

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.ProjectDescriptionPersistence
import io.cloudflight.jems.server.project.service.model.ProjectLongTermPlans
import io.cloudflight.jems.server.project.service.model.ProjectManagement
import io.cloudflight.jems.server.project.service.model.ProjectOverallObjective
import io.cloudflight.jems.server.project.service.model.ProjectPartnership
import io.cloudflight.jems.server.project.service.model.ProjectRelevance
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceSpfRecipient
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceStrategy
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceSynergy
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UpdateProjectDescriptionTest : UnitTest() {

    companion object {
        val projectOverallObjective: ProjectOverallObjective = mockk()
        val projectRelevance: ProjectRelevance = mockk()
        val projectPartnership: ProjectPartnership = mockk()
        val projectManagement: ProjectManagement = mockk()
        val projectLongTermPlans: ProjectLongTermPlans = mockk()
    }

    @MockK
    lateinit var persistence: ProjectDescriptionPersistence

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updateProjectDescription: UpdateProjectDescription

    @Test
    fun updateOverallObjective() {
        every { persistence.updateOverallObjective(1L, projectOverallObjective) } returns projectOverallObjective
        assertThat(updateProjectDescription.updateOverallObjective(1L, projectOverallObjective)).isEqualTo(projectOverallObjective)
    }

    @Test
    fun updateProjectRelevance() {
        val projectBenefits: List<ProjectRelevanceBenefit> = emptyList()
        val projectSpfRecipients: List<ProjectRelevanceSpfRecipient> = emptyList()
        val projectStrategies: List<ProjectRelevanceStrategy> = emptyList()
        val projectSynergy: List<ProjectRelevanceSynergy> = emptyList()

        every { projectRelevance.projectBenefits } returns projectBenefits
        every { projectRelevance.projectSpfRecipients } returns projectSpfRecipients
        every { projectRelevance.projectStrategies } returns projectStrategies
        every { projectRelevance.projectSynergies } returns projectSynergy

        every { generalValidator.maxSize(projectBenefits, 20, "benefits") } returns emptyMap()
        every { generalValidator.maxSize(projectSpfRecipients, 20, "spfRecipients") } returns emptyMap()
        every { generalValidator.maxSize(projectStrategies, 20, "strategies") } returns emptyMap()
        every { generalValidator.maxSize(projectSynergy, 20, "synergies") } returns emptyMap()

        every { generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxSize(projectBenefits, 20, "benefits"),
            generalValidator.maxSize(projectSpfRecipients, 20, "spfRecipients"),
            generalValidator.maxSize(projectStrategies, 20, "strategies"),
            generalValidator.maxSize(projectSynergy, 20, "synergies")
        ) } returns Unit

        every { persistence.updateProjectRelevance(1L, projectRelevance) } returns projectRelevance
        assertThat(updateProjectDescription.updateProjectRelevance(1L, projectRelevance)).isEqualTo(projectRelevance)
    }

    @Test
    fun updatePartnership() {
        every { persistence.updatePartnership(1L, projectPartnership) } returns projectPartnership
        assertThat(updateProjectDescription.updatePartnership(1L, projectPartnership)).isEqualTo(projectPartnership)
    }

    @Test
    fun updateProjectManagement() {
        every { persistence.updateProjectManagement(1L, projectManagement) } returns projectManagement
        assertThat(updateProjectDescription.updateProjectManagement(1L, projectManagement)).isEqualTo(projectManagement)
    }

    @Test
    fun updateProjectLongTermPlans() {
        every { persistence.updateProjectLongTermPlans(1L, projectLongTermPlans) } returns projectLongTermPlans
        assertThat(updateProjectDescription.updateProjectLongTermPlans(1L, projectLongTermPlans)).isEqualTo(projectLongTermPlans)
    }

}
