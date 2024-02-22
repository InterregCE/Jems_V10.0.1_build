package io.cloudflight.jems.server.project.service.report.project.closure.updateProjectClosure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosurePrize
import io.cloudflight.jems.server.project.service.report.project.closure.ProjectReportProjectClosurePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateProjectReportProjectClosureTest: UnitTest() {

    companion object {
        private const val REPORT_ID = 2L
        private val storyInputTranslation = setOf(InputTranslation(SystemLanguage.EN, "story EN"))
        private val prizeInputTranslation = setOf(InputTranslation(SystemLanguage.EN, "prize EN"))

        val projectClosure = ProjectReportProjectClosure(
            story = storyInputTranslation,
            prizes = listOf(
                ProjectReportProjectClosurePrize(
                    id = 99L,
                    prize = prizeInputTranslation,
                    orderNum = 1
                )
            )
        )
    }

    @MockK
    private lateinit var projectReportProjectClosurePersistence: ProjectReportProjectClosurePersistence

    @MockK
    private lateinit var generalValidatorService: GeneralValidatorService

    @InjectMockKs
    private lateinit var interactor: UpdateProjectReportProjectClosure

    @BeforeEach
    fun reset() {
        clearMocks(projectReportProjectClosurePersistence, generalValidatorService)
    }

    @Test
    fun updateProjectClosure() {
        every { projectReportProjectClosurePersistence.updateProjectReportProjectClosure(REPORT_ID, projectClosure) } returns projectClosure
        every { generalValidatorService.maxLength(storyInputTranslation, 5000, any()) } returns emptyMap()
        every { generalValidatorService.maxLength(prizeInputTranslation, 500, any()) } returns emptyMap()
        every { generalValidatorService.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidatorService.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        assertThat(interactor.update(REPORT_ID, projectClosure)).isEqualTo(projectClosure)
    }

    @Test
    fun `update - invalid number of prizes`() {
        var mockedPrizeList = mockk<List<ProjectReportProjectClosurePrize>>()
        every { mockedPrizeList.size } returns 101
        assertThrows<ProjectClosurePrizeLimitNumberExceededException> {
            interactor.update(REPORT_ID, projectClosure.copy(prizes = mockedPrizeList))
        }
    }

    @Test
    fun `update - invalid length of input text`() {
        val invalidStoryInput = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(5001)))
        every {
            generalValidatorService.throwIfAnyIsInvalid(
                generalValidatorService.maxLength(invalidStoryInput, 5000, "story"),
                generalValidatorService.maxLength(prizeInputTranslation, 500, "prize"),
            )
        } throws AppInputValidationException(hashMapOf())

        assertThrows<AppInputValidationException> {
            interactor.update(REPORT_ID, projectClosure.copy(story = invalidStoryInput))
        }
    }
}
