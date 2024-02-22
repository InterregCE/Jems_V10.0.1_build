package io.cloudflight.jems.server.project.controller.report.project.closure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.project.projectClosure.ProjectReportProjectClosureDTO
import io.cloudflight.jems.api.project.dto.report.project.projectClosure.ProjectReportProjectClosurePrizeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosurePrize
import io.cloudflight.jems.server.project.service.report.project.closure.getProjectClosure.GetProjectReportProjectClosureInteractor
import io.cloudflight.jems.server.project.service.report.project.closure.updateProjectClosure.UpdateProjectReportProjectClosureInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectReportProjectClosureControllerTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val REPORT_ID = 2L

        val projectClosure = ProjectReportProjectClosure(
            story = setOf(InputTranslation(SystemLanguage.EN, "story EN")),
            prizes = listOf(
                ProjectReportProjectClosurePrize(
                    id = 99L,
                    prize = setOf(InputTranslation(SystemLanguage.EN, "prize EN")),
                    orderNum = 1
                ),
                ProjectReportProjectClosurePrize(
                    id = 100L,
                    prize = setOf(InputTranslation(SystemLanguage.EN, "prize 2 EN")),
                    orderNum = 2
                )
            )
        )

        val projectClosureDTO = ProjectReportProjectClosureDTO(
            story = setOf(InputTranslation(SystemLanguage.EN, "story EN")),
            prizes = listOf(
                ProjectReportProjectClosurePrizeDTO(
                    id = 99L,
                    prize = setOf(InputTranslation(SystemLanguage.EN, "prize EN")),
                    orderNum = 1
                ),
                ProjectReportProjectClosurePrizeDTO(
                    id = 100L,
                    prize = setOf(InputTranslation(SystemLanguage.EN, "prize 2 EN")),
                    orderNum = 2
                )
            )
        )

        val projectClosureUpdate = ProjectReportProjectClosure(
            story = setOf(InputTranslation(SystemLanguage.EN, "story EN")),
            prizes = listOf(
                ProjectReportProjectClosurePrize(
                    id = null,
                    prize = setOf(InputTranslation(SystemLanguage.EN, "prize EN")),
                    orderNum = 1
                ),
                ProjectReportProjectClosurePrize(
                    id = null,
                    prize = setOf(InputTranslation(SystemLanguage.EN, "prize 2 EN")),
                    orderNum = 2
                )
            )
        )

        val projectClosureUpdateDTO = ProjectReportProjectClosureDTO(
            story = setOf(InputTranslation(SystemLanguage.EN, "story EN")),
            prizes = listOf(
                ProjectReportProjectClosurePrizeDTO(
                    id = null,
                    prize = setOf(InputTranslation(SystemLanguage.EN, "prize EN")),
                    orderNum = 1
                ),
                ProjectReportProjectClosurePrizeDTO(
                    id = null,
                    prize = setOf(InputTranslation(SystemLanguage.EN, "prize 2 EN")),
                    orderNum = 2
                )
            )
        )
    }

    @MockK
    private lateinit var getProjectReportProjectClosure: GetProjectReportProjectClosureInteractor

    @MockK
    private lateinit var updateProjectReportProjectClosure: UpdateProjectReportProjectClosureInteractor

    @InjectMockKs
    lateinit var controller: ProjectReportProjectClosureController

    @BeforeEach()
    fun reset() {
        clearMocks(getProjectReportProjectClosure, updateProjectReportProjectClosure)
    }

    @Test
    fun get() {
        every { getProjectReportProjectClosure.get(projectId = PROJECT_ID, REPORT_ID) } returns projectClosure
        assertThat(controller.getProjectClosure(projectId = PROJECT_ID, REPORT_ID)).isEqualTo(projectClosureDTO)
    }

    @Test
    fun update() {
        every { updateProjectReportProjectClosure.update(REPORT_ID, projectClosureUpdate) } returns projectClosure
        assertThat(controller.updateProjectClosure(projectId = PROJECT_ID, REPORT_ID, projectClosureUpdateDTO)).isEqualTo(projectClosureDTO)
    }
}
