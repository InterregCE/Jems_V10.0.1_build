package io.cloudflight.jems.server.project.service.report.partner.file.control

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class ControlReportFileAuthorizationServiceTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 350L
        private const val PARTNER_ID = 424L
        private const val USER_ID = 8L
    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK
    lateinit var filePersistence: JemsFilePersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var service: ControlReportFileAuthorizationService

    @BeforeEach
    fun reset() {
        clearMocks(partnerPersistence)
        clearMocks(filePersistence)
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @ParameterizedTest(name = "validateChangeToFileAllowed (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"])
    fun validateChangeToFileAllowed(status: ReportStatus) {
        val reportId = 41L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        val author = mockk<UserSimple>()
        every { author.id } returns USER_ID
        every { filePersistence
            .getFileAuthor(PARTNER_ID, "Project/000350/Report/Partner/000424/PartnerControlReport/000041/", 9558L)
        } returns author
        assertDoesNotThrow { service.validateChangeToFileAllowed(PARTNER_ID, reportId, 9558L, true) }
    }

    @ParameterizedTest(name = "validateChangeToFileAllowed - wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"], mode = EnumSource.Mode.EXCLUDE)
    fun `validateChangeToFileAllowed - wrong status`(status: ReportStatus) {
        val reportId = 45L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        assertThrows<ReportControlNotOpen> { service.validateChangeToFileAllowed(PARTNER_ID, reportId, 0L, true) }
    }

    @ParameterizedTest(name = "validateChangeToFileAllowed - open enough - file not found (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"])
    fun `validateChangeToFileAllowed - open enough - file not found`(status: ReportStatus) {
        val reportId = 49L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        every { filePersistence
            .getFileAuthor(PARTNER_ID, "Project/000350/Report/Partner/000424/PartnerControlReport/000049/", -1L)
        } returns null
        assertThrows<FileNotFound> { service.validateChangeToFileAllowed(PARTNER_ID, reportId, -1L, true) }
    }

    @ParameterizedTest(name = "validateChangeToFileAllowed - open enough (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl", "Certified"])
    fun `validateChangeToFileAllowed - open enough`(status: ReportStatus) {
        val reportId = 51L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        val author = mockk<UserSimple>()
        every { author.id } returns USER_ID
        every { filePersistence
            .getFileAuthor(PARTNER_ID, "Project/000350/Report/Partner/000424/PartnerControlReport/000051/", 9558L)
        } returns author
        assertDoesNotThrow { service.validateChangeToFileAllowed(PARTNER_ID, reportId, 9558L, false) }
    }

    @ParameterizedTest(name = "validateChangeToFileAllowed - open enough - wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl", "Certified"], mode = EnumSource.Mode.EXCLUDE)
    fun `validateChangeToFileAllowed - open enough - wrong status`(status: ReportStatus) {
        val reportId = 55L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        assertThrows<ReportControlNotStartedYet> { service.validateChangeToFileAllowed(PARTNER_ID, reportId, 0L, false) }
    }

    @ParameterizedTest(name = "validateChangeToFileAllowed - file not found (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl", "Certified"])
    fun `validateChangeToFileAllowed - file not found`(status: ReportStatus) {
        val reportId = 59L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        every { filePersistence
            .getFileAuthor(PARTNER_ID, "Project/000350/Report/Partner/000424/PartnerControlReport/000059/", -1L)
        } returns null
        assertThrows<FileNotFound> { service.validateChangeToFileAllowed(PARTNER_ID, reportId, -1L, false) }
    }

}
