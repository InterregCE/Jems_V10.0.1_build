package io.cloudflight.jems.server.project.service.result.get_project_result

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetProjectResultTest: UnitTest() {

    private val result1 = ProjectResult(
        resultNumber = 1,
        programmeResultIndicatorId = 74L,
        programmeResultIndicatorIdentifier = "DFG98",
        baseline = BigDecimal.ZERO,
        targetValue = BigDecimal.TEN,
        periodNumber = 4,
        description = setOf(InputTranslation(language = EN, translation = "EN desc")),
    )

    @MockK
    lateinit var persistence: ProjectResultPersistence

    @InjectMockKs
    lateinit var getProjectResult: GetProjectResult

    @Test
    fun getResultsForProject() {
        every { persistence.getResultsForProject(1L, null) } returns listOf(result1)
        assertThat(getProjectResult.getResultsForProject(1L, null)).containsExactly(result1.copy())
    }
}
