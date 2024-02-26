package io.cloudflight.jems.server.project.repository.report.project.closure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosurePrizeEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosurePrizeTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.closure.ProjectReportProjectClosureStoryTranslEntity
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosurePrize
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectReportProjectClosurePersistenceProviderTest: UnitTest() {

    companion object {
        private const val REPORT_ID = 99L

        private val closureStoryEntity = ProjectReportProjectClosureStoryEntity(
            reportId = REPORT_ID,
            translatedValues = mutableSetOf(
                ProjectReportProjectClosureStoryTranslEntity(TranslationId(mockk(), SystemLanguage.EN), "story")
            ),
        )

        private val closurePrizeEntity = ProjectReportProjectClosurePrizeEntity(
            id = 1L,
            reportId = REPORT_ID,
            sortNumber = 1,
            translatedValues = mutableSetOf(
                ProjectReportProjectClosurePrizeTranslEntity(TranslationId(mockk(), SystemLanguage.EN), "prize")
            ),
        )

        private val closure = ProjectReportProjectClosure(
            story = setOf(InputTranslation(SystemLanguage.EN, "story")),
            prizes = listOf(
                ProjectReportProjectClosurePrize(
                    id = 1L,
                    prize = setOf(InputTranslation(SystemLanguage.EN, "prize")),
                    orderNum = 1
                )
            )
        )

        private fun updatedClosure(isInput: Boolean) = ProjectReportProjectClosure(
            story = setOf(InputTranslation(SystemLanguage.EN, "updated story")),
            prizes = listOf(
                ProjectReportProjectClosurePrize(
                    id = 1L,
                    prize = setOf(InputTranslation(SystemLanguage.EN, "updated prize")),
                    orderNum = 1
                ),
                ProjectReportProjectClosurePrize(
                    id = if (isInput) null else 2L,
                    prize = setOf(InputTranslation(SystemLanguage.EN, "new prize")),
                    orderNum = 2
                )
            )
        )

        private val newPrizeEntity = ProjectReportProjectClosurePrizeEntity(
            id = 2L,
            reportId = REPORT_ID,
            sortNumber = 2,
            translatedValues = mutableSetOf(
                ProjectReportProjectClosurePrizeTranslEntity(TranslationId(mockk(), SystemLanguage.EN), "new prize")
            ),
        )
    }

    @MockK
    private lateinit var closureStoryRepository: ProjectReportProjectClosureStoryRepository

    @MockK
    private lateinit var closurePrizeRepository: ProjectReportProjectClosurePrizeRepository

    @InjectMockKs
    private lateinit var persistence: ProjectReportProjectClosurePersistenceProvider


    @BeforeEach
    fun reset() {
        clearMocks(closureStoryRepository, closurePrizeRepository)
    }

    @Test
    fun getProjectReportProjectClosure() {
        every { closureStoryRepository.getByReportId(REPORT_ID) } returns closureStoryEntity
        every { closurePrizeRepository.findAllByReportId(REPORT_ID) } returns listOf(closurePrizeEntity)

        assertThat(persistence.getProjectReportProjectClosure(REPORT_ID)).isEqualTo(closure)
    }

    @Test
    fun updateProjectReportProjectClosure() {
        val prizesSlot = slot<List<ProjectReportProjectClosurePrizeEntity>>()

        every { closureStoryRepository.getByReportId(REPORT_ID) } returns closureStoryEntity
        every { closurePrizeRepository.findAllByReportId(REPORT_ID) } returns listOf(closurePrizeEntity) andThen
            listOf(closurePrizeEntity, newPrizeEntity)
        every { closurePrizeRepository.saveAll(capture(prizesSlot)) } returnsArgument 0

        verify(exactly = 0) { closureStoryRepository.save(any()) }
        verify(exactly = 0) { closurePrizeRepository.delete(any()) }

        assertThat(persistence.updateProjectReportProjectClosure(REPORT_ID, updatedClosure(true)))
            .isEqualTo(updatedClosure(false))
        assertThat(prizesSlot.captured.size).isEqualTo(1)
    }

    @Test
    fun deleteProjectReportProjectClosure() {
        every { closureStoryRepository.deleteById(REPORT_ID) } returnsArgument 0
        every { closurePrizeRepository.deleteAllByReportId(REPORT_ID) } returnsArgument 0

        persistence.deleteProjectReportProjectClosure(REPORT_ID)
        verify(exactly = 1) { closureStoryRepository.deleteById(REPORT_ID) }
        verify(exactly = 1) { closurePrizeRepository.deleteAllByReportId(REPORT_ID) }
    }
}
