package io.cloudflight.jems.server.project.service.report.project.workPlan.uploadFileToProjectReportWorkPlan

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import io.mockk.CapturingSlot
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private const val PROJECT_ID = 450L
private const val REPORT_ID = 462L
private const val WP_ID = 481L
private const val USER_ID = 435L

private const val FILE_SIZE = 100L

internal class UploadFileToProjectReportWorkPlanTest : UnitTest() {

    private fun getDummyFile(name: String) = ProjectFile(mockk(), name = name, size = FILE_SIZE)

    @MockK lateinit var reportFilePersistence: ProjectReportFilePersistence
    @MockK lateinit var reportWorkPlanPersistence: ProjectReportWorkPlanPersistence
    @MockK lateinit var securityService: SecurityService

    @InjectMockKs lateinit var interactor: UploadFileToProjectReportWorkPlan

    @BeforeEach
    fun setup() {
        clearMocks(reportFilePersistence, reportWorkPlanPersistence, securityService)
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun uploadToActivity() {
        every { reportWorkPlanPersistence.existsByActivityId(PROJECT_ID, REPORT_ID, WP_ID, activityId = 10L) } returns true

        val newFile = slot<JemsFileCreate>()
        val mockedResult = mockk<JemsFileMetadata>()
        every { reportFilePersistence.updateReportActivityAttachment(10L, capture(newFile)) } returns mockedResult

        assertThat(
            interactor.uploadToActivity(PROJECT_ID, REPORT_ID, WP_ID, 10L, getDummyFile("new_file.pdf"))
        ).isEqualTo(mockedResult)

        assertBasicFileAttributes(newFile)
        with(newFile.captured) {
            assertThat(path).isEqualTo("Project/000450/Report/ProjectReport/000462/WorkPlanProjectReport" +
                "/WorkPackageProjectReport/000481/ActivityProjectReport/000010/")
            assertThat(type).isEqualTo(JemsFileType.ActivityProjectReport)
        }
    }

    @Test
    fun `uploadToActivity - not existing`() {
        every { reportWorkPlanPersistence.existsByActivityId(PROJECT_ID, REPORT_ID, WP_ID, activityId = -1L) } returns false
        assertThrows<ActivityNotFoundException> {
            interactor.uploadToActivity(PROJECT_ID, REPORT_ID, WP_ID, -1L, mockk())
        }
    }

    @Test
    fun `uploadToActivity - file type invalid`() {
        every { reportWorkPlanPersistence.existsByActivityId(PROJECT_ID, REPORT_ID, WP_ID, 12L) } returns true

        val file = mockk<ProjectFile>()
        every { file.name } returns "invalid.exe"

        assertThrows<FileTypeNotSupported> {
            interactor.uploadToActivity(PROJECT_ID, REPORT_ID, WP_ID, 12L, file)
        }
    }

    @Test
    fun uploadToDeliverable() {
        every {
            reportWorkPlanPersistence.existsByDeliverableId(
                PROJECT_ID,
                REPORT_ID,
                WP_ID,
                activityId = 10L,
                deliverableId = 12L
            )
        } returns true

        val newFile = slot<JemsFileCreate>()
        val mockedResult = mockk<JemsFileMetadata>()
        every { reportFilePersistence.updateReportDeliverableAttachment(12L, capture(newFile)) } returns mockedResult

        assertThat(
            interactor.uploadToDeliverable(PROJECT_ID, REPORT_ID, WP_ID, 10L, 12L, getDummyFile("new_file.pdf"))
        ).isEqualTo(mockedResult)

        assertBasicFileAttributes(newFile)
        with(newFile.captured) {
            assertThat(path).isEqualTo("Project/000450/Report/ProjectReport/000462/WorkPlanProjectReport/" +
                "WorkPackageProjectReport/000481/ActivityProjectReport/000010/DeliverableProjectReport/000012/")
            assertThat(type).isEqualTo(JemsFileType.DeliverableProjectReport)
        }
    }

    @Test
    fun `uploadToDeliverable - file type invalid`() {
        every { reportWorkPlanPersistence.existsByDeliverableId(PROJECT_ID, REPORT_ID, WP_ID, 10L, 14L) } returns true

        val file = mockk<ProjectFile>()
        every { file.name } returns "invalid.exe"

        assertThrows<FileTypeNotSupported> {
            interactor.uploadToDeliverable(PROJECT_ID, REPORT_ID, WP_ID, 10L, 14L, file)
        }
    }

    @Test
    fun `uploadToDeliverable - not existing`() {
        every {
            reportWorkPlanPersistence.existsByDeliverableId(
                PROJECT_ID,
                REPORT_ID,
                WP_ID,
                activityId = -1L,
                deliverableId = -1L
            )
        } returns false
        assertThrows<DeliverableNotFoundException> {
            interactor.uploadToDeliverable(PROJECT_ID, REPORT_ID, WP_ID, -1L, -1L, mockk())
        }
    }

    @Test
    fun uploadToOutput() {
        every { reportWorkPlanPersistence.existsByOutputId(PROJECT_ID, REPORT_ID, WP_ID, outputId = 15L) } returns true

        val newFile = slot<JemsFileCreate>()
        val mockedResult = mockk<JemsFileMetadata>()
        every { reportFilePersistence.updateReportOutputAttachment(15L, capture(newFile)) } returns mockedResult

        assertThat(
            interactor.uploadToOutput(PROJECT_ID, REPORT_ID, WP_ID, 15L, getDummyFile("new_file.pdf"))
        ).isEqualTo(mockedResult)

        assertBasicFileAttributes(newFile)
        with(newFile.captured) {
            assertThat(path).isEqualTo("Project/000450/Report/ProjectReport/000462/WorkPlanProjectReport/" +
                "WorkPackageProjectReport/000481/OutputProjectReport/000015/")
            assertThat(type).isEqualTo(JemsFileType.OutputProjectReport)
        }
    }

    @Test
    fun `uploadToOutput - not existing`() {
        every { reportWorkPlanPersistence.existsByOutputId(PROJECT_ID, REPORT_ID, WP_ID, outputId = -1L) } returns false
        assertThrows<OutputNotFoundException> {
            interactor.uploadToOutput(PROJECT_ID, REPORT_ID, WP_ID, -1L, mockk())
        }
    }

    @Test
    fun `uploadToOutput - file type invalid`() {
        every { reportWorkPlanPersistence.existsByOutputId(PROJECT_ID, REPORT_ID, WP_ID, 17L) } returns true

        val file = mockk<ProjectFile>()
        every { file.name } returns "invalid.exe"

        assertThrows<FileTypeNotSupported> {
            interactor.uploadToOutput(PROJECT_ID, REPORT_ID, WP_ID, 17L, file)
        }
    }

    private fun assertBasicFileAttributes(newFile: CapturingSlot<JemsFileCreate>) {
        with(newFile.captured) {
            assertThat(projectId).isEqualTo(PROJECT_ID)
            assertThat(partnerId).isNull()
            assertThat(name).isEqualTo("new_file.pdf")
            assertThat(size).isEqualTo(FILE_SIZE)
            assertThat(userId).isEqualTo(USER_ID)
        }
    }

}
