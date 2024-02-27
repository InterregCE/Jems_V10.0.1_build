package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportClosurePrizeEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportClosurePrizeTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportClosureStoryTranslEntity
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectReportProjectClosurePersistenceProviderTest: UnitTest() {

    companion object {
        private const val REPORT_ID = 99L

        private val closurePrizeEntity = ProjectReportClosurePrizeEntity(
            id = 1L,
            reportId = REPORT_ID,
            sortNumber = 1,
            translatedValues = mutableSetOf(
                ProjectReportClosurePrizeTranslEntity(TranslationId(mockk(), SystemLanguage.EN), "prize")
            ),
        )

        private val closure = ProjectReportProjectClosure(
            story = setOf(InputTranslation(SystemLanguage.EN, "story")),
            prizes = listOf(
                setOf(InputTranslation(SystemLanguage.EN, "prize"))
            )
        )

        private fun updatedClosure() = ProjectReportProjectClosure(
            story = setOf(InputTranslation(SystemLanguage.EN, "updated story")),
            prizes = listOf(
                setOf(InputTranslation(SystemLanguage.EN, "updated prize")),
                setOf(InputTranslation(SystemLanguage.EN, "new prize"))
            )
        )

    }

    @MockK
    private lateinit var closureStoryRepository: ProjectReportClosureStoryRepository

    @MockK
    private lateinit var closurePrizeRepository: ProjectReportClosurePrizeRepository

    @MockK
    private lateinit var projectReportRepository: ProjectReportRepository

    @InjectMockKs
    private lateinit var persistence: ProjectReportProjectClosurePersistenceProvider


    @BeforeEach
    fun reset() {
        clearMocks(closureStoryRepository, closurePrizeRepository, projectReportRepository)
    }

    @Test
    fun getProjectReportProjectClosure() {
        val report = mockk<ProjectReportEntity>()
        every { report.id } returns REPORT_ID
        val translation = mockk<TranslationId<ProjectReportEntity>>()
        every { translation.language } returns SystemLanguage.EN
        val storyTranslEntity = ProjectReportClosureStoryTranslEntity(translation, "story")
        every { projectReportRepository.getById(REPORT_ID) } returns report
        every { closureStoryRepository.findAllByTranslationIdSourceEntity(report) } returns mutableSetOf(storyTranslEntity)
        every { closurePrizeRepository.findAllByReportIdOrderBySortNumberAsc(REPORT_ID) } returns mutableListOf(closurePrizeEntity)

        assertThat(persistence.getProjectReportProjectClosure(REPORT_ID)).isEqualTo(closure)
    }

    @Test
    fun updateProjectReportProjectClosure() {
        val prizesSlot = slot<ProjectReportClosurePrizeEntity>()

        val report = mockk<ProjectReportEntity>()
        val translation = mockk<TranslationId<ProjectReportEntity>>()
        every { translation.language } returns SystemLanguage.EN
        val storyTranslEntity = ProjectReportClosureStoryTranslEntity(translation, "story")

        every { report.id } returns REPORT_ID
        every { projectReportRepository.getById(REPORT_ID) } returns report
        every { closurePrizeRepository.save(capture(prizesSlot)) } returnsArgument 0
        every { closureStoryRepository.findAllByTranslationIdSourceEntity(report) } returns mutableSetOf(storyTranslEntity)
        every { closurePrizeRepository.findAllByReportIdOrderBySortNumberAsc(REPORT_ID) } returns mutableListOf(closurePrizeEntity)
        every { closurePrizeRepository.deleteAll(listOf()) } returnsArgument 0


        verify(exactly = 0) { closureStoryRepository.save(any()) }
        verify(exactly = 0) { closurePrizeRepository.delete(any()) }

        assertThat(persistence.updateProjectReportProjectClosure(REPORT_ID, updatedClosure()))
            .isEqualTo(updatedClosure())
    }

    @Test
    fun deleteProjectReportProjectClosure() {
        val report = mockk<ProjectReportEntity>()
        every { report.id } returns REPORT_ID
        every { projectReportRepository.getById(REPORT_ID) } returns report

        every { closureStoryRepository.deleteAllByTranslationIdSourceEntity(report) } returnsArgument 0
        every { closurePrizeRepository.deleteAllByReportId(REPORT_ID) } returnsArgument 0

        persistence.deleteProjectReportProjectClosure(REPORT_ID)
        verify(exactly = 1) { closureStoryRepository.deleteAllByTranslationIdSourceEntity(report) }
        verify(exactly = 1) { closurePrizeRepository.deleteAllByReportId(REPORT_ID) }
    }
}
