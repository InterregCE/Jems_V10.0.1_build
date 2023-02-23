package io.cloudflight.jems.server.project.service.result.update_project_results

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.FI
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class UpdateProjectResultsTest : UnitTest() {

    private val result1 = ProjectResult(
        resultNumber = 1,
        programmeResultIndicatorId = 30L,
        programmeResultIndicatorIdentifier = "05PO",
        baseline = BigDecimal.ZERO,
        targetValue = BigDecimal.ONE,
        periodNumber = 7,
        description = setOf(InputTranslation(language = FI, translation = "FI desc")),
        deactivated = false
    )

    fun projectSummary(projectId: Long, status: ApplicationStatus): ProjectSummary {
        return ProjectSummary(
            id = projectId,
            customIdentifier = "test",
            callName = "call",
            acronym = "test",
            status = status,
        )
    }

    @MockK
    lateinit var persistence: ProjectResultPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @MockK
    lateinit var projectPersistence: ProjectPersistenceProvider

    @MockK
    lateinit var veryBigResultsList: List<ProjectResult>

    @InjectMockKs
    lateinit var updateProjectResult: UpdateProjectResults


    @BeforeEach
    fun reset() {
        clearMocks(generalValidator)
    }

    @Test
    fun updateResultsForProject() {
        every { persistence.updateResultsForProject(1L, any()) } returnsArgument 1
        every { persistence.getAvailablePeriodNumbers(1L) } returns setOf(7)
        every { projectPersistence.getProjectSummary(1L) } returns projectSummary(3L, ApplicationStatus.DRAFT)

        Assertions.assertThat(updateProjectResult.updateResultsForProject(1L, listOf(result1)))
            .containsExactly(result1)
    }

    @Test
    fun `updateResultsForProject - empty periods`() {
        every { persistence.updateResultsForProject(2L, any()) } returnsArgument 1
        every { persistence.getAvailablePeriodNumbers(2L) } returns emptySet()
        every { projectPersistence.getProjectSummary(2L) } returns projectSummary(3L, ApplicationStatus.DRAFT)
        val result = result1.copy(periodNumber = null)
        Assertions.assertThat(updateProjectResult.updateResultsForProject(2L, listOf(result)))
            .containsExactly(result)
    }

    @Test
    fun `update results when max allowed results amount reached`() {
        every { veryBigResultsList.size } returns 21
        val exception = assertThrows<MaxNumberOrResultPerProjectException> {
            updateProjectResult.updateResultsForProject(
                2L,
                veryBigResultsList
            )
        }
        Assertions.assertThat(exception.i18nMessage.i18nKey)
            .isEqualTo("use.case.update.project.results.max.allowed.reached")
    }

    @Test
    fun `update results - empty results should pass`() {
        every { persistence.updateResultsForProject(3L, any()) } returns emptyList()
        every { projectPersistence.getProjectSummary(3L) } returns projectSummary(3L, ApplicationStatus.DRAFT)
        assertDoesNotThrow { updateProjectResult.updateResultsForProject(3L, emptyList()) }
    }

    @Test
    fun `update results when period does not exist`() {
        val result = result1.copy(
            targetValue = null, // null is valid here
            periodNumber = 3
        )
        every { persistence.getAvailablePeriodNumbers(12L) } returns setOf(1, 2)

        val exception =
            assertThrows<PeriodNotFoundException> { updateProjectResult.updateResultsForProject(12L, listOf(result)) }
        Assertions.assertThat(exception.i18nMessage.i18nKey)
            .isEqualTo("use.case.update.project.results.period.not.found")
    }

    @Test
    fun `should validate input before updating the results`() {
        val result2 = result1.copy(
            resultNumber = 2, targetValue = null, periodNumber = 3, baseline = BigDecimal.TEN, description = emptySet()
        )
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { true }) } throws AppInputValidationException(
            emptyMap()
        )

        assertThrows<AppInputValidationException> {
            updateProjectResult.updateResultsForProject(12L, listOf(result1, result2))
        }

        verify(exactly = 1) { generalValidator.maxLength(result1.description, 1000, "description") }
        verify(exactly = 1) { generalValidator.maxLength(result2.description, 1000, "description") }
        verify(exactly = 1) {
            generalValidator.numberBetween(
                result1.targetValue, BigDecimal.ZERO, BigDecimal.valueOf(999_999_999_99, 2), "targetValue"
            )
        }
        verify(exactly = 1) {
            generalValidator.numberBetween(
                result2.targetValue, BigDecimal.ZERO, BigDecimal.valueOf(999_999_999_99, 2), "targetValue"
            )
        }
        verify(exactly = 1) { generalValidator.scale(result1.targetValue, 2, "targetValue") }
        verify(exactly = 1) { generalValidator.scale(result2.targetValue, 2, "targetValue") }

        verify(exactly = 1) {
            generalValidator.numberBetween(
                result1.baseline, BigDecimal.ZERO, BigDecimal.valueOf(999_999_999_99, 2), "baseline"
            )
        }
        verify(exactly = 1) {
            generalValidator.numberBetween(
                result2.baseline, BigDecimal.ZERO, BigDecimal.valueOf(999_999_999_99, 2), "baseline"
            )
        }
        verify(exactly = 1) { generalValidator.scale(result1.baseline, 2, "baseline") }
        verify(exactly = 1) { generalValidator.scale(result2.baseline, 2, "baseline") }
    }

    @Test
    fun `updateResultsForProject - when project is contracted`() {
        every { persistence.updateResultsForProject(5L, any()) } returnsArgument 1
        every { persistence.getAvailablePeriodNumbers(5L) } returns setOf(7)
        every { projectPersistence.getProjectSummary(5L) } returns projectSummary(5L, ApplicationStatus.CONTRACTED)
        every { persistence.getResultsForProject(5L, null) } returns listOf(result1)

        val updated = result1.copy(deactivated = true)
        Assertions.assertThat(updateProjectResult.updateResultsForProject(5L, listOf(updated)))
            .containsExactly(updated)
    }

    @Test
    fun `updateResultsForProject - try to deactivate when project is not contracted yet`() {
        every { persistence.updateResultsForProject(5L, any()) } returnsArgument 1
        every { persistence.getAvailablePeriodNumbers(5L) } returns setOf(7)
        every { projectPersistence.getProjectSummary(5L) } returns projectSummary(5L, ApplicationStatus.DRAFT)
        every { persistence.getResultsForProject(5L, null) } returns listOf(result1)

        val updated = result1.copy(deactivated = true)
        assertThrows<ProjectResultDeactivationNotAllowedException> { updateProjectResult.updateResultsForProject(5L, listOf(updated)) }
    }

    @Test
    fun `updateResultsForProject - try to delete when project is contracted`() {
        every { persistence.updateResultsForProject(5L, any()) } returnsArgument 1
        every { persistence.getAvailablePeriodNumbers(5L) } returns setOf(7)
        every { projectPersistence.getProjectSummary(5L) } returns projectSummary(5L, ApplicationStatus.CONTRACTED)
        every { persistence.getResultsForProject(5L, null) } returns listOf(result1)

        assertThrows<ProjectResultDeletionNotAllowedException> { updateProjectResult.updateResultsForProject(5L, listOf()) }
    }

    @Test
    fun `updateResultsForProject - try to activate a project result`() {
        val deactivated = result1.copy(deactivated = true)
        every { persistence.updateResultsForProject(5L, any()) } returnsArgument 1
        every { persistence.getAvailablePeriodNumbers(5L) } returns setOf(7)
        every { projectPersistence.getProjectSummary(5L) } returns projectSummary(5L, ApplicationStatus.CONTRACTED)
        every { persistence.getResultsForProject(5L, null) } returns listOf(deactivated)
        assertThrows<ProjectResultActivationNotAllowedException> { updateProjectResult.updateResultsForProject(5L, listOf(result1)) }
    }
}
