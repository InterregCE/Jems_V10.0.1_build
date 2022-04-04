package io.cloudflight.jems.server.project.service.report.partner.workPlan.uploadFileToProjectPartnerReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.workPlan.ProjectReportWorkPlanPersistence
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
private const val PARTNER_ID = 478L
private const val REPORT_ID = 462L
private const val WP_ID = 481L
private const val USER_ID = 435L

private const val FILE_SIZE = 100L

internal class UploadFileToProjectPartnerReportWorkPlanTest : UnitTest() {

    private fun getDummyFile(name: String) = ProjectFile(mockk(), name = name, size = FILE_SIZE)

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence
    @MockK
    lateinit var reportWorkPlanPersistence: ProjectReportWorkPlanPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadFileToProjectPartnerReportWorkPlan

    @BeforeEach
    fun setup() {
        clearMocks(partnerPersistence)
        clearMocks(securityService)
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun uploadToActivity() {
        every { reportWorkPlanPersistence.existsByActivityId(PARTNER_ID, REPORT_ID, WP_ID, activityId = 10L) } returns true

        val newFile = slot<ProjectReportFileCreate>()
        val mockedResult = mockk<ProjectReportFileMetadata>()
        every { reportFilePersistence.updatePartnerReportActivityAttachment(10L, capture(newFile)) } returns mockedResult

        assertThat(
            interactor.uploadToActivity(PARTNER_ID, REPORT_ID, WP_ID, 10L, getDummyFile("new_file.pdf"))
        ).isEqualTo(mockedResult)

        assertBasicFileAttributes(newFile)
        with(newFile.captured) {
            assertThat(path).isEqualTo("Project/000450/Report/Partner/000478/PartnerReport/000462/WorkPlan/WorkPackage/000481/Activity/000010/")
            assertThat(type).isEqualTo(ProjectPartnerReportFileType.Activity)
        }
    }

    @Test
    fun `uploadToActivity - not existing`() {
        every { reportWorkPlanPersistence.existsByActivityId(PARTNER_ID, REPORT_ID, WP_ID, activityId = -1L) } returns false
        assertThrows<ActivityNotFoundException> {
            interactor.uploadToActivity(PARTNER_ID, REPORT_ID, WP_ID, -1L, mockk())
        }
    }

    @Test
    fun uploadToDeliverable() {
        every { reportWorkPlanPersistence.existsByDeliverableId(PARTNER_ID, REPORT_ID, WP_ID, activityId = 10L, deliverableId = 12L) } returns true

        val newFile = slot<ProjectReportFileCreate>()
        val mockedResult = mockk<ProjectReportFileMetadata>()
        every { reportFilePersistence.updatePartnerReportDeliverableAttachment(12L, capture(newFile)) } returns mockedResult

        assertThat(
            interactor.uploadToDeliverable(PARTNER_ID, REPORT_ID, WP_ID, 10L, 12L, getDummyFile("new_file.pdf"))
        ).isEqualTo(mockedResult)

        assertBasicFileAttributes(newFile)
        with(newFile.captured) {
            assertThat(path).isEqualTo("Project/000450/Report/Partner/000478/PartnerReport/000462/WorkPlan/WorkPackage/000481/Activity/000010/Deliverable/000012/")
            assertThat(type).isEqualTo(ProjectPartnerReportFileType.Deliverable)
        }
    }

    @Test
    fun `uploadToDeliverable - not existing`() {
        every { reportWorkPlanPersistence.existsByDeliverableId(PARTNER_ID, REPORT_ID, WP_ID, activityId = -1L, deliverableId = -1L) } returns false
        assertThrows<DeliverableNotFoundException> {
            interactor.uploadToDeliverable(PARTNER_ID, REPORT_ID, WP_ID, -1L, -1L, mockk())
        }
    }

    @Test
    fun uploadToOutput() {
        every { reportWorkPlanPersistence.existsByOutputId(PARTNER_ID, REPORT_ID, WP_ID, outputId = 15L) } returns true

        val newFile = slot<ProjectReportFileCreate>()
        val mockedResult = mockk<ProjectReportFileMetadata>()
        every { reportFilePersistence.updatePartnerReportOutputAttachment(15L, capture(newFile)) } returns mockedResult

        assertThat(
            interactor.uploadToOutput(PARTNER_ID, REPORT_ID, WP_ID, 15L, getDummyFile("new_file.pdf"))
        ).isEqualTo(mockedResult)

        assertBasicFileAttributes(newFile)
        with(newFile.captured) {
            assertThat(path).isEqualTo("Project/000450/Report/Partner/000478/PartnerReport/000462/WorkPlan/WorkPackage/000481/Output/000015/")
            assertThat(type).isEqualTo(ProjectPartnerReportFileType.Output)
        }
    }

    @Test
    fun `uploadToOutput - not existing`() {
        every { reportWorkPlanPersistence.existsByOutputId(PARTNER_ID, REPORT_ID, WP_ID, outputId = -1L) } returns false
        assertThrows<OutputNotFoundException> {
            interactor.uploadToOutput(PARTNER_ID, REPORT_ID, WP_ID, -1L, mockk())
        }
    }

    private fun assertBasicFileAttributes(newFile: CapturingSlot<ProjectReportFileCreate>) {
        with(newFile.captured) {
            assertThat(projectId).isEqualTo(PROJECT_ID)
            assertThat(partnerId).isEqualTo(PARTNER_ID)
            assertThat(name).isEqualTo("new_file.pdf")
            assertThat(size).isEqualTo(FILE_SIZE)
            assertThat(userId).isEqualTo(USER_ID)
        }
    }

}
