package io.cloudflight.jems.server.project.service.report.partner.expenditure.uploadFileToProjectPartnerReportExpenditureTest

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.uploadFileToProjectPartnerReportExpenditure.ExpenditureNotFoundException
import io.cloudflight.jems.server.project.service.report.partner.expenditure.uploadFileToProjectPartnerReportExpenditure.FileTypeNotSupported
import io.cloudflight.jems.server.project.service.report.partner.expenditure.uploadFileToProjectPartnerReportExpenditure.UploadFileToProjectPartnerReportExpenditure
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

private const val PARTNER_ID = 3500L
private const val REPORT_ID = 360L
private const val USER_ID = 698L

class UploadFileToProjectPartnerReportExpenditureTest : UnitTest() {
    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence
    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadFileToProjectPartnerReportExpenditure

    @BeforeEach
    fun setup() {
        clearMocks(reportFilePersistence)
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }


    @Test
    fun uploadToExpenditure() {
        every { reportExpenditurePersistence.existsByExpenditureId(PARTNER_ID, REPORT_ID, 22L) } returns true
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns 800L

        val slotFile = slot<ProjectReportFileCreate>()
        val mockResult = mockk<ProjectReportFileMetadata>()
        every { reportFilePersistence.updatePartnerReportExpenditureAttachment(22L, capture(slotFile)) } returns mockResult

        val file = ProjectFile(ByteArray(5).inputStream(), "file_name.xlsx", 50L)

        assertThat(interactor.uploadToExpenditure(PARTNER_ID, REPORT_ID, 22L, file)).isEqualTo(mockResult)

        with(slotFile.captured) {
            assertThat(projectId).isEqualTo(800L)
            assertThat(partnerId).isEqualTo(PARTNER_ID)
            assertThat(name).isEqualTo("file_name.xlsx")
            assertThat(path).isEqualTo("Project/000800/Report/Partner/003500/PartnerReport/000360/Expenditure/000022/")
            assertThat(type).isEqualTo(ProjectPartnerReportFileType.Expenditure)
            assertThat(size).isEqualTo(50L)
            assertThat(userId).isEqualTo(USER_ID)
        }
    }

    @Test
    fun `uploadToExpenditure - not exists`() {
        every { reportExpenditurePersistence.existsByExpenditureId(PARTNER_ID, REPORT_ID, -1L) } returns false
        assertThrows<ExpenditureNotFoundException> { interactor.uploadToExpenditure(PARTNER_ID, REPORT_ID, -1L, mockk()) }
    }

    @Test
    fun `uploadToExpenditure - file type invalid`() {
        every { reportExpenditurePersistence.existsByExpenditureId(PARTNER_ID, REPORT_ID, 25L) } returns true

        val file = mockk<ProjectFile>()
        every { file.name } returns "invalid.exe"

        assertThrows<FileTypeNotSupported> {
            interactor.uploadToExpenditure(PARTNER_ID, REPORT_ID, 25L, file)
        }
    }

}
