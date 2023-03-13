package io.cloudflight.jems.server.project.repository.report.project.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportProjectResultEntity
import io.cloudflight.jems.server.project.repository.report.project.resultPrinciple.ProjectReportProjectResultRepository
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class ProjectReportFilePersistenceProviderTest : UnitTest() {

    companion object {
        val projectResultEntity = ProjectReportProjectResultEntity(
            id = 1L,
            projectReport = mockk(),
            resultNumber = 7,
            periodNumber = 9,
            programmeResultIndicatorEntity = null,
            baseline = BigDecimal.valueOf(1),
            targetValue = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            previouslyReported = BigDecimal.valueOf(4),
            attachment = mockk(),
            deactivated = false
        )

        val jesmFileCreate = JemsFileCreate(
            projectId = 4L,
            partnerId = null,
            name = "file-name",
            path = "file-path",
            type = JemsFileType.ProjectResult,
            size = 5L,
            content = mockk(),
            userId = 1L,
        )
    }

    @MockK
    private lateinit var fileService: JemsProjectFileService

    @MockK
    private lateinit var projectResultRepository: ProjectReportProjectResultRepository

    @InjectMockKs
    private lateinit var persistence: ProjectReportFilePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(fileService, projectResultRepository)
    }

    @Test
    fun updateProjectResultAttachment() {
        val reportId = 5L
        val resultNumber = 7
        val newFileMetadata = JemsFileMetadata(9L, jesmFileCreate.name, ZonedDateTime.now())

        every { projectResultRepository.findByProjectReportIdAndResultNumber(reportId, resultNumber) } returns projectResultEntity
        every { fileService.delete(eq(projectResultEntity.attachment!!)) } returns Unit
        every { fileService.persistProjectFileAndPerformAction(eq(jesmFileCreate), any()) } returns newFileMetadata

        Assertions.assertThat(persistence.updateProjectResultAttachment(reportId, resultNumber, jesmFileCreate))
            .isEqualTo(newFileMetadata)
    }
}
