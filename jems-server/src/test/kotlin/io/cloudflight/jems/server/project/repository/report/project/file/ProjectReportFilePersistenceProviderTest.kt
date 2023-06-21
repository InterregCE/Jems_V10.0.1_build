package io.cloudflight.jems.server.project.repository.report.project.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.entity.report.project.resultPrinciple.ProjectReportProjectResultEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.report.project.resultPrinciple.ProjectReportProjectResultRepository
import io.cloudflight.jems.server.project.repository.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.project.workPlan.ProjectReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.project.workPlan.ProjectReportWorkPackageOutputRepository
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class ProjectReportFilePersistenceProviderTest : UnitTest() {

    companion object {

        private fun activity(id: Long, attachment: JemsFileMetadataEntity?) = ProjectReportWorkPackageActivityEntity(
            id = id,
            workPackageEntity = mockk(),
            number = 1,
            deactivated = false,
            activityId = null,
            startPeriodNumber = null,
            endPeriodNumber = null,
            status = null,
            attachment = attachment,
            previousStatus = null
        )

        private fun deliverable(id: Long, attachment: JemsFileMetadataEntity?) = ProjectReportWorkPackageActivityDeliverableEntity(
            id = id,
            activityEntity = mockk(),
            number = 1,
            deactivated = false,
            deliverableId = null,
            periodNumber = null,
            previouslyReported = BigDecimal.ZERO,
            currentReport = BigDecimal.ZERO,
            attachment = attachment,
            previousCurrentReport = BigDecimal.ZERO,
        )

        private fun output(id: Long, attachment: JemsFileMetadataEntity?) = ProjectReportWorkPackageOutputEntity(
            id = id,
            workPackageEntity = mockk(),
            number = 1,
            deactivated = false,
            programmeOutputIndicator = null,
            periodNumber = null,
            targetValue = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
            currentReport = BigDecimal.ZERO,
            attachment = attachment,
            previousCurrentReport = BigDecimal.ZERO,
        )

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

        private fun fileCreate(name: String = "new_file.txt", type: JemsFileType) = JemsFileCreate(
            projectId = 6666L,
            partnerId = null,
            name = name,
            path = "our/indexed/path/",
            type = type,
            size = 400L,
            content = mockk(),
            userId = 555L,
        )
    }

    @MockK
    private lateinit var workPlanActivityRepository: ProjectReportWorkPackageActivityRepository
    @MockK
    private lateinit var workPlanActivityDeliverableRepository: ProjectReportWorkPackageActivityDeliverableRepository
    @MockK
    private lateinit var workPlanOutputRepository: ProjectReportWorkPackageOutputRepository
    @MockK
    private lateinit var projectResultRepository: ProjectReportProjectResultRepository
    @MockK
    private lateinit var fileService: JemsProjectFileService

    @InjectMockKs
    private lateinit var persistence: ProjectReportFilePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(
            workPlanActivityRepository,
            workPlanActivityDeliverableRepository,
            workPlanOutputRepository,
            projectResultRepository,
            fileService,
        )
    }

    @Test
    fun updateReportActivityAttachment() {
        val oldFile = mockk<JemsFileMetadataEntity>()

        every { workPlanActivityRepository.getById(80L) } returns activity(id = 80L, attachment = oldFile)

        val fileCreate = fileCreate(type = JemsFileType.ActivityProjectReport)
        val resultMock = mockk<JemsFileMetadata>()
        every { fileService.persistFileAndPerformAction(fileCreate, any()) } returns resultMock
        mockFileDeletion(oldFile)

        assertThat(persistence.updateReportActivityAttachment(80L, file = fileCreate)).isEqualTo(resultMock)
    }

    @Test
    fun updateReportDeliverableAttachment() {
        val oldFile = mockk<JemsFileMetadataEntity>()

        every { workPlanActivityDeliverableRepository.getById(75L) } returns deliverable(id = 75L, attachment = oldFile)

        val fileCreate = fileCreate(type = JemsFileType.DeliverableProjectReport)
        val resultMock = mockk<JemsFileMetadata>()
        every { fileService.persistFileAndPerformAction(fileCreate, any()) } returns resultMock
        mockFileDeletion(oldFile)

        assertThat(persistence.updateReportDeliverableAttachment(75L, file = fileCreate)).isEqualTo(resultMock)
    }

    @Test
    fun updateReportOutputAttachment() {
        val oldFile = mockk<JemsFileMetadataEntity>()

        every { workPlanOutputRepository.getById(70L) } returns output(id = 70L, attachment = oldFile)

        val fileCreate = fileCreate(type = JemsFileType.OutputProjectReport)
        val resultMock = mockk<JemsFileMetadata>()
        every { fileService.persistFileAndPerformAction(fileCreate, any()) } returns resultMock
        mockFileDeletion(oldFile)

        assertThat(persistence.updateReportOutputAttachment(70L, file = fileCreate)).isEqualTo(resultMock)
    }

    @Test
    fun updateProjectResultAttachment() {
        val reportId = 5L
        val resultNumber = 7
        val newFileMetadata = JemsFileMetadata(9L, jesmFileCreate.name, ZonedDateTime.now())

        every { projectResultRepository.findByProjectReportIdAndResultNumber(reportId, resultNumber) } returns projectResultEntity
        every { fileService.delete(eq(projectResultEntity.attachment!!)) } returns Unit
        every { fileService.persistFileAndPerformAction(eq(jesmFileCreate), any()) } returns newFileMetadata

        assertThat(persistence.updateProjectResultAttachment(reportId, resultNumber, jesmFileCreate))
            .isEqualTo(newFileMetadata)
    }

    private fun mockFileDeletion(
        oldFile: JemsFileMetadataEntity,
    ) {
        every { oldFile.minioBucket } returns "bucket"
        every { oldFile.minioLocation } returns "remove/me.pdf"

        every { fileService.delete(oldFile) } answers { }
    }

}
