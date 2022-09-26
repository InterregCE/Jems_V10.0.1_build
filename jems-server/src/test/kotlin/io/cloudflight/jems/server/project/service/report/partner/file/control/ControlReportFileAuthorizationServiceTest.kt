package io.cloudflight.jems.server.project.service.report.partner.file.control

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
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
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence
    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var service: ControlReportFileAuthorizationService

    @BeforeEach
    fun reset() {
        clearMocks(partnerPersistence)
        clearMocks(reportFilePersistence)
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
        every { reportFilePersistence
            .getFileAuthor(PARTNER_ID, "Project/000350/Report/Partner/000424/PartnerControlReport/000041/", 9558L)
        } returns author
        assertDoesNotThrow { service.validateChangeToFileAllowed(PARTNER_ID, reportId, 9558L) }
    }

    @ParameterizedTest(name = "validateChangeToFileAllowed - wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"], mode = EnumSource.Mode.EXCLUDE)
    fun `validateChangeToFileAllowed - wrong status`(status: ReportStatus) {
        val reportId = 45L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        assertThrows<ReportNotInControl> { service.validateChangeToFileAllowed(PARTNER_ID, reportId, 0L) }
    }

    @ParameterizedTest(name = "validateChangeToFileAllowed - file not found (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"])
    fun `validateChangeToFileAllowed - file not found`(status: ReportStatus) {
        val reportId = 49L
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        every { reportFilePersistence
            .getFileAuthor(PARTNER_ID, "Project/000350/Report/Partner/000424/PartnerControlReport/000049/", -1L)
        } returns null
        assertThrows<FileNotFound> { service.validateChangeToFileAllowed(PARTNER_ID, reportId, -1L) }
    }

}
