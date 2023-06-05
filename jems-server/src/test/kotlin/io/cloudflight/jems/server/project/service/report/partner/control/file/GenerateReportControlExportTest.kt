package io.cloudflight.jems.server.project.service.report.partner.control.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport.GenerateReportControlExport
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport.GenerateReportControlExportException
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport.ReportControlExportService
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserSettings
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.io.IOException
import java.time.ZonedDateTime

class GenerateReportControlExportTest : UnitTest() {


    companion object {
        const val pluginKey = "standard-partner-control-report-export-plugin"
        const val PARTNER_ID = 1L
        const val PROJECT_ID = 1L
        const val REPORT_ID = 3L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)

        val CONTROLER_ROLE = UserRole(id = 7, name = "controller", permissions = emptySet(), isDefault = false)
        val userController = User(
            id = 3,
            email = "controller@jems.eu",
            userSettings = UserSettings(sendNotificationsToEmail = false),
            name = "John",
            surname = "Doe",
            userRole = CONTROLER_ROLE,
            userStatus = UserStatus.ACTIVE
        )
        val localControllerUser = LocalCurrentUser(
            userController, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + CONTROLER_ROLE.name)
            )
        )


        val partnerReport = ProjectPartnerReport(
            id = REPORT_ID,
            reportNumber = 3,
            status = ReportStatus.Certified,
            version = "1",
            firstSubmission = YESTERDAY,
            lastResubmission = null,
            lastControlReopening = null,
            projectReportId = 23L,
            projectReportNumber = 230,
            identification = PartnerReportIdentification(
                projectIdentifier = "CLF00001",
                projectAcronym = "acronym",
                partnerNumber = 1,
                partnerAbbreviation = "CBG",
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                coFinancing = listOf(),
                nameInEnglish = "Costa-Bianco Group",
                nameInOriginalLanguage = "Costa-Bianco Group",
                legalStatus = null,
                partnerType = null,
                vatRecovery = null,
                country = null,
                currency = null
            ),
            controlEnd = null
        )
    }

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var reportControlExportService: ReportControlExportService

    @InjectMockKs
    lateinit var generateReportControlExport: GenerateReportControlExport



    @Test
    fun `throws generate certificate exception`() {
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns partnerReport
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, partnerReport.version) } returns PROJECT_ID

        val exception = GenerateReportControlExportException(IOException())
        every { reportControlExportService.generate(partnerReport, PARTNER_ID, PROJECT_ID, pluginKey) } throws exception

        assertThrows<GenerateReportControlExportException> {
            generateReportControlExport.export(PARTNER_ID, REPORT_ID, pluginKey)
        }

    }

}
