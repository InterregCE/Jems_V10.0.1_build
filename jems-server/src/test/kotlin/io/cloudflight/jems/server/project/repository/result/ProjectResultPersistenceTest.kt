package io.cloudflight.jems.server.project.repository.result

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.BE
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.NO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorTranslEntity
import io.cloudflight.jems.server.programme.repository.indicator.ResultIndicatorRepository
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.entity.ProjectPeriodRow
import io.cloudflight.jems.server.project.entity.TranslationResultId
import io.cloudflight.jems.server.project.entity.result.ProjectResultEntity
import io.cloudflight.jems.server.project.entity.result.ProjectResultId
import io.cloudflight.jems.server.project.entity.result.ProjectResultRow
import io.cloudflight.jems.server.project.entity.result.ProjectResultTransl
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil.Companion.project
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Optional
import javax.persistence.EntityNotFoundException

class ProjectResultPersistenceTest: UnitTest() {

    companion object {
        private const val INDICATOR_ID = 5L
        private val resultId1 = ProjectResultId(projectId = project.id, resultNumber = 1)
        private val resultId2 = ProjectResultId(projectId = project.id, resultNumber = 2)

        private fun trIdRes(resultId: ProjectResultId, lang: SystemLanguage) = TranslationResultId(
            resultId = resultId,
            language = lang
        )

        val indicatorResult = ResultIndicatorEntity(
            id = INDICATOR_ID,
            identifier = "IND05",
            programmePriorityPolicyEntity = ProgrammeSpecificObjectiveEntity(ProgrammeObjectivePolicy.AdvancedTechnologies, ""),
            translatedValues = mutableSetOf(),
        ).apply {
            translatedValues.addAll(listOf(
                ResultIndicatorTranslEntity(TranslationId(this, BE), "IND05 name", "IND05 measurement unit", "IND05 source"),
            ))
        }

        val result1_model = ProjectResult(
            resultNumber = 1,
            programmeResultIndicatorId = INDICATOR_ID,
            programmeResultIndicatorIdentifier = "IND05",
            programmeResultName = setOf(InputTranslation(language = BE, translation = "IND05 name")),
            programmeResultMeasurementUnit = setOf(InputTranslation(language = BE, translation = "IND05 measurement unit")),
            baseline = BigDecimal.ZERO,
            targetValue = BigDecimal.ONE,
            periodNumber = 10,
            periodStartMonth = 21,
            periodEndMonth = 22,
            description = setOf(
                InputTranslation(language = BE, translation = "BE desc")
            )
        )

        val update1_model = ProjectResult(
            resultNumber = 1,
            programmeResultIndicatorId = INDICATOR_ID,
            programmeResultIndicatorIdentifier = "IND05",
            baseline = BigDecimal.ZERO,
            targetValue = BigDecimal.ONE,
            periodNumber = 10,
            description = setOf(
                InputTranslation(language = BE, translation = "BE desc"),
                InputTranslation(language = NO, translation = ""),
                InputTranslation(language = SK, translation = null)
            ),
        )

        val result2_model = ProjectResult(
            baseline = BigDecimal.ZERO,
            resultNumber = 2,
            periodNumber = 20,
        )

        val result1 = ProjectResultEntity(
            resultId = resultId1,
            translatedValues = setOf(
                ProjectResultTransl(translationId = trIdRes(resultId1, BE), description = "BE desc"),
                ProjectResultTransl(translationId = trIdRes(resultId1, NO), description = ""),
                ProjectResultTransl(translationId = trIdRes(resultId1, SK), description = null)
            ),
            periodNumber = 10,
            programmeResultIndicatorEntity = indicatorResult,
            baseline = BigDecimal.ZERO,
            targetValue = BigDecimal.ONE,
        )

        val result2 = ProjectResultEntity(
            baseline = BigDecimal.ZERO,
            resultId = resultId2,
            periodNumber = 20,
        )

    }

    @RelaxedMockK
    lateinit var projectRepository: ProjectRepository

    @RelaxedMockK
    lateinit var indicatorRepository: ResultIndicatorRepository

    @RelaxedMockK
    lateinit var projectResultRepository: ProjectResultRepository

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    private lateinit var projectVersionUtils: ProjectVersionUtils

    private lateinit var persistence: ProjectResultPersistenceProvider

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = ProjectResultPersistenceProvider(
            projectRepository,
            indicatorRepository,
            projectVersionUtils,
            projectResultRepository
        )
    }

    @Test
    fun `get project results - not-existing project`() {
        every { projectRepository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getResultsForProject(-1, null) }
        assertThat(ex.entity).isEqualTo("project")
    }

    @Test
    fun `project results are correctly mapped and sorted`() {
        every { projectRepository.findById(eq(1L)) } returns Optional.of(project.copy(
            results = setOf(result2, result1),
            periods = listOf(ProjectPeriodEntity(ProjectPeriodId(1L, 10), start = 21, end = 22)),
        ))
        assertThat(persistence.getResultsForProject(1L, null)).containsExactly(
            result1_model, result2_model,
        )
    }

    @Test
    fun `get project results - with previous version`() {
        val projectId = 1L
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val version = "2.0"
        val mockPRRow: ProjectResultRow = mockk()
        every { mockPRRow.resultNumber } returns result1_model.resultNumber
        every { mockPRRow.baseline } returns result1_model.baseline
        every { mockPRRow.programmeResultIndicatorId } returns result1_model.programmeResultIndicatorId
        every { mockPRRow.programmeResultIndicatorIdentifier } returns result1_model.programmeResultIndicatorIdentifier
        every { mockPRRow.programmeResultIndicatorLanguage } returns BE
        every { mockPRRow.programmeResultIndicatorName } returns "IND05 name"
        every { mockPRRow.programmeResultIndicatorMeasurementUnit } returns "IND05 measurement unit"
        every { mockPRRow.targetValue } returns result1_model.targetValue
        every { mockPRRow.periodNumber } returns result1_model.periodNumber
        every { mockPRRow.language } returns BE
        every { mockPRRow.description } returns "BE desc"

        every { projectVersionRepo.findTimestampByVersion(projectId, version) } returns timestamp
        every { projectRepository.findPeriodsByProjectIdAsOfTimestamp(projectId, timestamp) } returns listOf(Period(10, 21, 22))
        every { projectResultRepository.getProjectResultsByProjectId(projectId, timestamp) } returns listOf(mockPRRow)

        assertThat(persistence.getResultsForProject(projectId, version)).containsExactly(
            result1_model
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
    fun `updateResultsForProject - test if repository save() is called with correct arguments`() {
        val projectSlot = slot<ProjectEntity>()
        every { projectRepository.findById(project.id) } returns Optional.of(project)
        every { indicatorRepository.getOne(INDICATOR_ID) } returns indicatorResult
        every { projectRepository.save(capture(projectSlot)) } returnsArgument 0

        persistence.updateResultsForProject(projectId = project.id, projectResults = listOf(update1_model, result2_model))

        assertThat(projectSlot.captured.results).containsExactly(
            result1,
            result2,
        )
    }

    @Test
    fun `updateResultsForProject - not existing indicator`() {
        every { projectRepository.findById(project.id) } returns Optional.of(project)
        every { indicatorRepository.getOne(-1) } throws EntityNotFoundException()

        assertThrows<EntityNotFoundException> { persistence.updateResultsForProject(
            projectId = project.id,
            projectResults = listOf(update1_model.copy(programmeResultIndicatorId = -1))
        ) }
    }

}

internal data class Period(
    override val periodNumber: Int?,
    override val periodStart: Int?,
    override val periodEnd: Int?,
) : ProjectPeriodRow
