import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerControlReportCertificatePlugin
import io.cloudflight.jems.plugin.contract.models.common.UserSummaryData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate.GenerateReportControlCertificate
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate.GenerateReportControlCertificateException
import io.cloudflight.jems.server.resources.service.get_logos.GetLogosInteractor
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserSettings
import io.cloudflight.jems.server.user.service.model.UserStatus
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
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.io.IOException
import java.time.ZonedDateTime

class GenerateReportControlCertificateTest : UnitTest() {


    companion object {
        const val pluginKey = "standard-partner-control-report-certificate-generate-plugin"
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
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @MockK
    lateinit var getLogosInteractor: GetLogosInteractor

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var partnerControlReportCertificatePlugin: PartnerControlReportCertificatePlugin

    @MockK
    lateinit var projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var generateReportControlCertificate: GenerateReportControlCertificate


    @BeforeEach
    fun setup() {

        every { securityService.currentUser } returns localControllerUser
        every { securityService.getUserIdOrThrow() } returns localControllerUser.user.id
        clearMocks(auditPublisher)
    }

    @Test
    fun `Should generate the control report certificate`() {
        val exportResult = ExportResult("pdf", "Control Certificate - CLF00001 - LP1 - R3.pdf", byteArrayOf())
        val currentUserSummaryData = UserSummaryData(
            id = userController.id,
            email = userController.email,
            name = userController.name,
            surname = userController.surname
        )
        val identifier = 89L

        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns partnerReport
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, partnerReport.version) } returns PROJECT_ID
        every { jemsPluginRegistry.get(PartnerControlReportCertificatePlugin::class, pluginKey) } returns partnerControlReportCertificatePlugin
        every {
            projectPartnerReportControlFilePersistence.countReportControlFilesByFileType(REPORT_ID, JemsFileType.ControlCertificate)
        } returns identifier - 1
        every {
            getLogosInteractor.getLogos()
        } returns listOf()

        every {
            partnerControlReportCertificatePlugin.generateCertificate(
                PROJECT_ID,
                PARTNER_ID,
                REPORT_ID,
                logo = null,
                creationDate = any(),
                currentUser = currentUserSummaryData
            )
        } returns exportResult

        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns mockk()


        val slot = slot<JemsFileCreate>()
        every { projectPartnerReportControlFilePersistence.saveReportControlFile(REPORT_ID, capture(slot)) } returns mockk()

        generateReportControlCertificate.generateCertificate(PARTNER_ID, REPORT_ID, pluginKey)

        verify(exactly = 1) { projectPartnerReportControlFilePersistence.saveReportControlFile(REPORT_ID, slot.captured) }
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate.action).isEqualTo(AuditAction.CONTROL_REPORT_CERTIFICATE_GENERATED)
        assertThat(slotAudit.captured.auditCandidate.description).isEqualTo("A control certificate was generated for partner report R.3 of partner LP1")

        assertThat(slot.captured.type).isEqualTo(JemsFileType.ControlCertificate)
        assertThat(slot.captured.name).matches("Control Certificate $identifier - CLF00001 - LP1 - R3.pdf")
    }

    @Test
    fun `throws generate certificate exception`() {
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns partnerReport
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, partnerReport.version) } returns PROJECT_ID
        val exception = GenerateReportControlCertificateException(IOException())
        every { jemsPluginRegistry.get(PartnerControlReportCertificatePlugin::class, pluginKey) } throws exception

        assertThrows<GenerateReportControlCertificateException> {
            generateReportControlCertificate.generateCertificate(PARTNER_ID, REPORT_ID, pluginKey)
        }

    }


}
