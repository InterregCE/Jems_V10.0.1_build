package io.cloudflight.jems.server.project.controller.result

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.result.InputProjectResultDTO
import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.result.get_project_result.GetProjectResultInteractor
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.result.update_project_result.UpdateProjectResultInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectResultControllerTest: UnitTest() {

    companion object {
        val description = setOf(
            InputTranslation(language = EN, translation = null),
            InputTranslation(language = CS, translation = "cs_desc"),
            InputTranslation(language = SK, translation = ""),
        )
        val result1 = ProjectResult(
            resultNumber = 1,
            programmeResultIndicatorId = 5L,
            programmeResultIndicatorIdentifier = "ABB05",
            targetValue = BigDecimal.ONE,
            periodNumber = 4,
            description = description
        )
        val result2 = ProjectResult(
            resultNumber = 2,
        )
    }

    @MockK
    lateinit var getResult: GetProjectResultInteractor

    @MockK
    lateinit var updateResult: UpdateProjectResultInteractor

    @InjectMockKs
    private lateinit var controller: ProjectResultController

    @Test
    fun getProjectResults() {
        every { getResult.getResultsForProject(1L, null) } returns listOf(result1, result2)

        assertThat(controller.getProjectResults(1L)).containsExactly(
            ProjectResultDTO(
                resultNumber = 1,
                programmeResultIndicatorId = 5L,
                programmeResultIndicatorIdentifier = "ABB05",
                targetValue = BigDecimal.ONE,
                periodNumber = 4,
                description = description
            ),
            ProjectResultDTO(
                resultNumber = 2,
            )
        )
    }

    @Test
    fun updateActivities() {
        val resultSlot = slot<List<ProjectResult>>()
        every { updateResult.updateResultsForProject(1L, capture(resultSlot)) } returns emptyList()

        val resultDto1 = InputProjectResultDTO(
            programmeResultIndicatorId = 15L,
            targetValue = BigDecimal.ONE,
            periodNumber = 7,
            description = setOf(InputTranslation(EN, "en desc"), InputTranslation(CS, ""), InputTranslation(SK, null)),
        )
        val resultDto2 = InputProjectResultDTO()

        controller.updateProjectResults(1L, listOf(resultDto1, resultDto2))
        assertThat(resultSlot.captured).containsExactly(
            ProjectResult(
                programmeResultIndicatorId = 15L,
                targetValue = BigDecimal.ONE,
                periodNumber = 7,
                description = setOf(InputTranslation(EN, "en desc"), InputTranslation(CS, ""), InputTranslation(SK, null)),
            ),
            ProjectResult()
        )
    }

}
