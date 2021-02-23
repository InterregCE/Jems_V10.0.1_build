package io.cloudflight.jems.server.project.repository.result

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.BE
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.NO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.repository.indicator.ResultIndicatorRepository
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.entity.TranslationResultId
import io.cloudflight.jems.server.project.entity.result.ProjectResultEntity
import io.cloudflight.jems.server.project.entity.result.ProjectResultId
import io.cloudflight.jems.server.project.entity.result.ProjectResultTransl
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil.Companion.project
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.result.model.ProjectResultTranslatedValue
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.Optional
import javax.persistence.EntityNotFoundException

class ProjectResultPersistenceTest: UnitTest() {

    companion object {
        private const val INDICATOR_ID = 5L
        val resultId1 = ProjectResultId(projectId = project.id, resultNumber = 1)
        val resultId2 = ProjectResultId(projectId = project.id, resultNumber = 2)

        private fun trIdRes(resultId: ProjectResultId, lang: SystemLanguage) = TranslationResultId(
            resultId = resultId,
            language = lang
        )

        val indicatorResult = ResultIndicatorEntity(
            id = INDICATOR_ID,
            identifier = "IND05",
            name = "Indicator Nr. 5",
            programmePriorityPolicyEntity = ProgrammeSpecificObjectiveEntity(ProgrammeObjectivePolicy.AdvancedTechnologies, ""),
        )

        val result1_model = ProjectResult(
            resultNumber = 1,
            programmeResultIndicatorId = INDICATOR_ID,
            programmeResultIndicatorIdentifier = "IND05",
            targetValue = BigDecimal.ONE,
            periodNumber = 10,
            translatedValues = setOf(
                ProjectResultTranslatedValue(language = BE, description = "BE desc"),
                ProjectResultTranslatedValue(language = NO, description = ""),
                ProjectResultTranslatedValue(language = SK, description = null),
            ),
        )

        val result2_model = ProjectResult(
            resultNumber = 2,
            periodNumber = 20,
        )

        val result1 = ProjectResultEntity(
            resultId = resultId1,
            translatedValues = setOf(
                ProjectResultTransl(translationId = trIdRes(resultId1, BE), description = "BE desc"),
                ProjectResultTransl(translationId = trIdRes(resultId1, NO), description = ""),
                ProjectResultTransl(translationId = trIdRes(resultId1, SK), description = null),
            ),
            periodNumber = 10,
            programmeResultIndicatorEntity = indicatorResult,
            targetValue = BigDecimal.ONE,
        )

        val result2 = ProjectResultEntity(
            resultId = resultId2,
            periodNumber = 20,
        )

    }

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository

    @RelaxedMockK
    lateinit var indicatorRepository: ResultIndicatorRepository

    @InjectMockKs
    private lateinit var persistence: ProjectResultPersistenceProvider

    @Test
    fun `get project results - not-existing project`() {
        every { projectRepository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getResultsForProject(-1) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun `project results are correctly mapped and sorted`() {
        every { projectRepository.findById(eq(1)) } returns Optional.of(project.copy(
            results = setOf(result2, result1)
        ))
        assertThat(persistence.getResultsForProject(1L)).containsExactly(
            result1_model, result2_model,
        )
    }

    @Test
    fun getAvailablePeriodNumbers() {
        every { projectRepository.findById(eq(1)) } returns Optional.of(project.copy(periods = listOf(
            ProjectPeriodEntity(id = ProjectPeriodId(project.id, 1), start = 1, end = 3),
            ProjectPeriodEntity(id = ProjectPeriodId(project.id, 2), start = 4, end = 6),
            ProjectPeriodEntity(id = ProjectPeriodId(project.id, 3), start = 7, end = 9),
        )))
        assertThat(persistence.getAvailablePeriodNumbers(1L)).containsExactly(1, 2, 3)
    }

    @Test
    fun `updateWorkPackageOutputs - test if repository save() is called with correct arguments`() {
        val projectSlot = slot<ProjectEntity>()
        every { projectRepository.findById(project.id) } returns Optional.of(project)
        every { indicatorRepository.getOne(INDICATOR_ID) } returns indicatorResult
        every { projectRepository.save(capture(projectSlot)) } returnsArgument 0

        persistence.updateResultsForProject(projectId = project.id, projectResults = listOf(result1_model, result2_model))

        assertThat(projectSlot.captured.results).containsExactly(
            result1,
            result2,
        )
    }

    @Test
    fun `updateWorkPackageOutputs - not existing indicator`() {
        every { projectRepository.findById(project.id) } returns Optional.of(project)
        every { indicatorRepository.getOne(-1) } throws EntityNotFoundException()

        assertThrows<EntityNotFoundException> { persistence.updateResultsForProject(
            projectId = project.id,
            projectResults = listOf(result1_model.copy(programmeResultIndicatorId = -1))
        ) }
    }

}
