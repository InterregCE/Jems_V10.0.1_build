package io.cloudflight.jems.server.project.repository.report.project.resultPrinciple

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect.PositiveEffects
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportHorizontalPrincipleEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportHorizontalPrincipleTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportProjectResultEntity
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportProjectResultTranslEntity
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectHorizontalPrinciples
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrincipleUpdate
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultUpdate
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class ProjectReportResultPrinciplePersistenceProviderTest : UnitTest() {

    companion object {
        private val time = ZonedDateTime.now()

        val expectedResultsAndPrinciple = ProjectReportResultPrinciple(
            projectResults = listOf(
                ProjectReportProjectResult(
                    resultNumber = 1,
                    programmeResultIndicatorId = 441L,
                    programmeResultIndicatorIdentifier = "indic-iden",
                    programmeResultIndicatorName = setOf(InputTranslation(SystemLanguage.EN, "waaaw")),
                    baseline = BigDecimal.valueOf(1),
                    targetValue = BigDecimal.valueOf(2),
                    currentReport = BigDecimal.valueOf(3),
                    previouslyReported = BigDecimal.valueOf(4),
                    periodDetail = ProjectPeriod(9, 3, 5),
                    description = setOf(InputTranslation(SystemLanguage.CS, "desc-CS")),
                    measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "measurement")),
                    attachment = JemsFileMetadata(45L, "test.txt", time),
                    deactivated = false
                )
            ),
            horizontalPrinciples = ProjectHorizontalPrinciples(PositiveEffects, PositiveEffects, PositiveEffects),
            sustainableDevelopmentDescription = setOf(InputTranslation(SystemLanguage.FR, "FR-sust OLD")),
            equalOpportunitiesDescription = setOf(InputTranslation(SystemLanguage.FR, "FR-equal OLD")),
            sexualEqualityDescription = emptySet(),
        )

        val expectedResultsAndPrinciplesAfterUpdate = ProjectReportResultPrinciple(
            projectResults = listOf(
                expectedResultsAndPrinciple.projectResults.first().copy(
                    currentReport = BigDecimal.valueOf(42L, 1),
                    description = setOf(
                        InputTranslation(SystemLanguage.CS, "desc-CS-new"),
                        InputTranslation(SystemLanguage.MK, "desc-MK-new"),
                    ),
                    periodDetail = ProjectPeriod(9, 13, 15),
                ),
            ),
            horizontalPrinciples = ProjectHorizontalPrinciples(PositiveEffects, PositiveEffects, PositiveEffects),
            sustainableDevelopmentDescription = setOf(InputTranslation(SystemLanguage.FR, "new FR sust")),
            equalOpportunitiesDescription = setOf(InputTranslation(SystemLanguage.FR, "new FR equal")),
            sexualEqualityDescription = setOf(InputTranslation(SystemLanguage.FR, "new FR sex")),
        )

        fun projectResultEntity(projectId: Long): ProjectReportProjectResultEntity {
            val projectReport = mockk<ProjectReportEntity>()
            every { projectReport.projectId } returns projectId
            every { projectReport.applicationFormVersion } returns "2.4.1"

            val attachment = mockk<JemsFileMetadataEntity>()
            every { attachment.id } returns 45L
            every { attachment.name } returns "test.txt"
            every { attachment.uploaded } returns time

            val indicator = mockk<ResultIndicatorEntity>()
            every { indicator.id } returns 441L
            every { indicator.identifier } returns "indic-iden"
            every { indicator.translatedValues } returns mutableSetOf(
                ResultIndicatorTranslEntity(
                    translationId = TranslationId(mockk(), SystemLanguage.EN),
                    name = "waaaw",
                    measurementUnit = "measurement",
                )
            )

            return ProjectReportProjectResultEntity(
                id = 1L,
                projectReport = projectReport,
                resultNumber = 1,
                translatedValues = mutableSetOf(ProjectReportProjectResultTranslEntity(TranslationId(mockk(), SystemLanguage.CS), "desc-CS")),
                periodNumber = 9,
                programmeResultIndicatorEntity = indicator,
                baseline = BigDecimal.valueOf(1),
                targetValue = BigDecimal.valueOf(2),
                currentReport = BigDecimal.valueOf(3),
                previouslyReported = BigDecimal.valueOf(4),
                attachment = attachment,
                deactivated = false
            )
        }

        fun horizontalPrincipleEntity() = ProjectReportHorizontalPrincipleEntity(
            reportId = 13L,
            projectReport = mockk(),
            sustainableDevelopmentCriteriaEffect = PositiveEffects,
            equalOpportunitiesEffect = PositiveEffects,
            sexualEqualityEffect = PositiveEffects,
            translatedValues = mutableSetOf(
                ProjectReportHorizontalPrincipleTranslEntity(
                    TranslationId(mockk(), SystemLanguage.FR),
                    sustainableDevelopmentDescription = "FR-sust OLD",
                    equalOpportunitiesDescription = "FR-equal OLD",
                    sexualEqualityDescription = null,
                ),
            ),
        )

    }

    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @MockK
    private lateinit var projectResultRepository: ProjectReportProjectResultRepository

    @MockK
    private lateinit var horizontalPrincipleRepository: ProjectReportHorizontalPrincipleRepository

    @MockK
    private lateinit var fileService: JemsProjectFileService

    @InjectMockKs
    private lateinit var persistence: ProjectReportResultPrinciplePersistenceProvider


    @BeforeEach
    fun reset() {
        clearMocks(projectPersistence, projectResultRepository, horizontalPrincipleRepository, fileService)
    }

    @Test
    fun get() {
        val projectId = 7L
        val reportId = 13L
        every { projectResultRepository.findByProjectReportIdIn(setOf(reportId)) } returns listOf(projectResultEntity(projectId))
        every { projectPersistence.getProjectPeriods(projectId, "2.4.1") } returns listOf(ProjectPeriod(9, 3, 5))
        every { horizontalPrincipleRepository.getByProjectReportId(reportId) } returns horizontalPrincipleEntity()

        assertThat(persistence.getProjectResultPrinciples(projectId = projectId, reportId))
            .isEqualTo(expectedResultsAndPrinciple)
    }

    @Test
    fun update() {
        val projectId = 8L
        val reportId = 14L
        val result = projectResultEntity(projectId)
        val horizontalPrinciples = horizontalPrincipleEntity()
        every { projectResultRepository.findByProjectReportIdIn(setOf(reportId)) } returns listOf(result)
        every { projectPersistence.getProjectPeriods(projectId, "2.4.1") } returns listOf(ProjectPeriod(9, 13, 15))
        every { horizontalPrincipleRepository.getByProjectReportId(reportId) } returns horizontalPrinciples

        val newValues = ProjectReportResultPrincipleUpdate(
            projectResults = mapOf(
                1 to ProjectReportResultUpdate(
                    BigDecimal.valueOf(42L, 1),
                    setOf(InputTranslation(SystemLanguage.CS, "desc-CS-new"), InputTranslation(SystemLanguage.MK, "desc-MK-new"))
                ),
            ),
            sustainableDevelopmentDescription = setOf(InputTranslation(SystemLanguage.FR, "new FR sust")),
            equalOpportunitiesDescription = setOf(InputTranslation(SystemLanguage.FR, "new FR equal")),
            sexualEqualityDescription = setOf(InputTranslation(SystemLanguage.FR, "new FR sex")),
        )
        assertThat(persistence.updateProjectReportResultPrinciple(projectId = projectId, reportId, newValues))
            .isEqualTo(expectedResultsAndPrinciplesAfterUpdate)

        assertThat(result.currentReport).isEqualTo(BigDecimal.valueOf(42L, 1))
    }

    @Test
    fun deleteProjectResultPrinciplesIfExist() {
        val result = mockk<ProjectReportProjectResultEntity>()
        every { projectResultRepository.findByProjectReportIdIn(setOf(755L)) } returns listOf(result)
        val attachment = mockk<JemsFileMetadataEntity>()
        every { result.attachment } returns attachment

        every { fileService.delete(attachment) } answers { }
        val deletedResultsSlot = slot<Iterable<ProjectReportProjectResultEntity>>()
        every { projectResultRepository.deleteAll(capture(deletedResultsSlot)) } answers { }

        every { horizontalPrincipleRepository.existsById(755L) } returns true
        every { horizontalPrincipleRepository.deleteById(755L) } answers { }

        persistence.deleteProjectResultPrinciplesIfExist(755L)

        verify(exactly = 1) { fileService.delete(attachment) }
        assertThat(deletedResultsSlot.captured).containsExactly(result)
        verify(exactly = 1) { horizontalPrincipleRepository.deleteById(755L) }
    }

}
