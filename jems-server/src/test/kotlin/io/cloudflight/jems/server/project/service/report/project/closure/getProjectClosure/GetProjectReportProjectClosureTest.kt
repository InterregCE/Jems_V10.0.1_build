package io.cloudflight.jems.server.project.service.report.project.closure.getProjectClosure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure
import io.cloudflight.jems.server.project.service.report.project.closure.ProjectReportProjectClosurePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetProjectReportProjectClosureTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val REPORT_ID = 2L

        val projectClosure = ProjectReportProjectClosure(
            story = setOf(InputTranslation(SystemLanguage.EN, "story EN")),
            prizes = listOf(
                setOf(InputTranslation(SystemLanguage.EN, "prize EN")),
                setOf(InputTranslation(SystemLanguage.EN, "prize 2 EN"))
            )
        )
    }

    @MockK
    private lateinit var projectReportProjectClosurePersistence: ProjectReportProjectClosurePersistence

    @InjectMockKs
    private lateinit var interactor: GetProjectReportProjectClosure

    @BeforeEach
    fun reset() {
        clearMocks(projectReportProjectClosurePersistence)
    }

    @Test
    fun getProjectClosure() {
        every { projectReportProjectClosurePersistence.getProjectReportProjectClosure(REPORT_ID) } returns projectClosure
        assertThat(interactor.get(PROJECT_ID, REPORT_ID)).isEqualTo(projectClosure)
    }
}
