package io.cloudflight.jems.server.project.service.result.update_project_result

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.FI
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.result.model.ProjectResultTranslatedValue
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class UpdateProjectResultTest: UnitTest() {

    private val result1 = ProjectResult(
        resultNumber = 1,
        programmeResultIndicatorId = 30L,
        programmeResultIndicatorIdentifier = "05PO",
        targetValue = BigDecimal.ONE,
        periodNumber = 7,
        translatedValues = setOf(ProjectResultTranslatedValue(language = FI, description = "FI desc")),
    )

    @MockK
    lateinit var persistence: ProjectResultPersistence

    @MockK
    lateinit var veryBigResultsList: List<ProjectResult>

    @InjectMockKs
    lateinit var updateProjectResult: UpdateProjectResult

    @Test
    fun updateResultsForProject() {
        every { persistence.updateResultsForProject(1L, any()) } returnsArgument 1
        every { persistence.getAvailablePeriodNumbers(1L) } returns setOf(7)
        Assertions.assertThat(updateProjectResult.updateResultsForProject(1L, listOf(result1)))
            .containsExactly(result1)
    }

    @Test
    fun `update results when max allowed results amount reached`() {
        every { veryBigResultsList.size } returns 21
        val exception = assertThrows<I18nValidationException> { updateProjectResult.updateResultsForProject(2L, veryBigResultsList) }
        Assertions.assertThat(exception.i18nKey).isEqualTo("project.results.max.allowed.reached")
    }

    @Test
    fun `update results - empty results should pass`() {
        every { persistence.updateResultsForProject(3L, any()) } returns emptyList()
        assertDoesNotThrow { updateProjectResult.updateResultsForProject(3L, emptyList()) }
    }

    @Test
    fun `update results when description is too long`() {
        val translation = ProjectResultTranslatedValue(
            language = FI,
            description = getStringOfLength(501)
        )
        val toBeSaved = listOf(ProjectResult(translatedValues = setOf(translation)))
        val exception = assertThrows<I18nValidationException> { updateProjectResult.updateResultsForProject(4L, toBeSaved) }
        Assertions.assertThat(exception.i18nKey).isEqualTo("project.results.description.size.too.long")
    }

    @Test
    fun `update results when invalid targetValue`() {
        assertTargetValueThrowException(BigDecimal.valueOf(999_999_999_990, 3))
        assertTargetValueThrowException(BigDecimal.valueOf(999_999_999_991, 3))
        assertTargetValueThrowException(BigDecimal.valueOf(1_000_000_000_00, 2))
        assertTargetValueThrowException(BigDecimal.valueOf(-1))
    }

    private fun assertTargetValueThrowException(value: BigDecimal) {
        val toBeSaved = listOf(ProjectResult(targetValue = value))
        val exception = assertThrows<I18nValidationException> { updateProjectResult.updateResultsForProject(10L, toBeSaved) }
        Assertions.assertThat(exception.i18nKey).isEqualTo("project.results.targetValue.not.valid")
    }

    @Test
    fun `update results when period does not exist`() {
        val result = result1.copy(
            targetValue = null, // null is valid here
            periodNumber = 3
        )
        every { persistence.getAvailablePeriodNumbers(12L) } returns setOf(1, 2)

        val exception = assertThrows<I18nValidationException> { updateProjectResult.updateResultsForProject(12L, listOf(result)) }
        Assertions.assertThat(exception.i18nKey).isEqualTo("project.results.period.does.not.exist")
    }

}
