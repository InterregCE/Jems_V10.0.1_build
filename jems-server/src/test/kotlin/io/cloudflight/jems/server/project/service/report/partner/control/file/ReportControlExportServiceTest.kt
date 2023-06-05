package io.cloudflight.jems.server.project.service.report.partner.control.file

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerControlReportExportPlugin
import io.cloudflight.jems.plugin.contract.models.common.UserSummaryData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
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
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport.GenerateReportControlExportException
import io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport.ReportControlExportService
import io.cloudflight.jems.server.resources.service.get_logos.GetLogosInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.io.IOException
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class ReportControlExportServiceTest : UnitTest() {

    companion object {
        const val pluginKey = "standard-partner-control-report-export-plugin"
        const val PARTNER_ID = 1L
        const val PROJECT_ID = 1L
        const val REPORT_ID = 3L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)

        val partnerReport = ProjectPartnerReport(
            id = REPORT_ID,
            reportNumber = 3,
            status = ReportStatus.Certified,
            version = "1",
            firstSubmission = YESTERDAY,
            lastResubmission = null,
            lastControlReopening = null,
            projectReportId = 29L,
            projectReportNumber = 290,
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
    lateinit var partnerControlReportExportPlugin: PartnerControlReportExportPlugin

    @MockK
    lateinit var projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var reportControlExportService: ReportControlExportService

    @BeforeEach
    fun setup() {
        every { securityService.currentUser } returns GenerateReportControlExportTest.localControllerUser
        every { securityService.getUserIdOrThrow() } returns GenerateReportControlExportTest.localControllerUser.user.id
        clearMocks(auditPublisher)
    }

    @Test
    fun `Should generate the control report export`() {
        val exportResult = ExportResult("pdf", "Control Report - CLF00001 - LP1 - R3.pdf", byteArrayOf())
        val currentUserSummaryData = UserSummaryData(
            id = GenerateReportControlExportTest.userController.id,
            email = GenerateReportControlExportTest.userController.email,
            name = GenerateReportControlExportTest.userController.name,
            surname = GenerateReportControlExportTest.userController.surname
        )
        val identifier = 73L

        every {
            reportPersistence.getPartnerReportById(
                partnerId = GenerateReportControlExportTest.PARTNER_ID,
                reportId = GenerateReportControlExportTest.REPORT_ID
            )
        } returns GenerateReportControlExportTest.partnerReport
        every {
            partnerPersistence.getProjectIdForPartnerId(
                GenerateReportControlExportTest.PARTNER_ID,
                GenerateReportControlExportTest.partnerReport.version
            )
        } returns GenerateReportControlExportTest.PROJECT_ID
        every {
            jemsPluginRegistry.get(
                PartnerControlReportExportPlugin::class,
                GenerateReportControlExportTest.pluginKey
            )
        } returns partnerControlReportExportPlugin
        every {
            projectPartnerReportControlFilePersistence.countReportControlFilesByFileType(
                GenerateReportControlExportTest.REPORT_ID, JemsFileType.ControlReport
            )
        } returns identifier - 1
        every {
            getLogosInteractor.getLogos()
        } returns listOf()

        every {
            partnerControlReportExportPlugin.export(
                GenerateReportControlExportTest.PROJECT_ID,
                GenerateReportControlExportTest.PARTNER_ID,
                GenerateReportControlExportTest.REPORT_ID,
                logo = null,
                creationDate = any(),
                currentUser = currentUserSummaryData
            )
        } returns exportResult

        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns mockk()


        val slot = slot<JemsFileCreate>()
        every {
            projectPartnerReportControlFilePersistence.saveReportControlFile(
                GenerateReportControlExportTest.REPORT_ID,
                capture(slot)
            )
        } returns mockk()

        reportControlExportService.generate(
            report = partnerReport, partnerId = PARTNER_ID, projectId = PROJECT_ID, pluginKey
        )

        verify(exactly = 1) {
            projectPartnerReportControlFilePersistence.saveReportControlFile(
                GenerateReportControlExportTest.REPORT_ID, slot.captured
            )
        }
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        Assertions.assertThat(slotAudit.captured.auditCandidate.action)
            .isEqualTo(AuditAction.CONTROL_REPORT_EXPORT_GENERATED)
        Assertions.assertThat(slotAudit.captured.auditCandidate.description)
            .isEqualTo("A control report was generated for partner report R.3 of partner LP1")

        Assertions.assertThat(slot.captured.type).isEqualTo(JemsFileType.ControlReport)
        Assertions.assertThat(slot.captured.name).matches("Control Report $identifier - CLF00001 - LP1 - R3.pdf")
    }

    @Test
    fun `throws generate certificate exception`() {
        every {
            reportPersistence.getPartnerReportById(
                partnerId = GenerateReportControlExportTest.PARTNER_ID,
                reportId = GenerateReportControlExportTest.REPORT_ID
            )
        } returns GenerateReportControlExportTest.partnerReport
        every {
            partnerPersistence.getProjectIdForPartnerId(
                GenerateReportControlExportTest.PARTNER_ID,
                GenerateReportControlExportTest.partnerReport.version
            )
        } returns GenerateReportControlExportTest.PROJECT_ID
        val exception = GenerateReportControlExportException(IOException())
        every {
            jemsPluginRegistry.get(
                PartnerControlReportExportPlugin::class,
                GenerateReportControlExportTest.pluginKey
            )
        } throws exception

        assertThrows<GenerateReportControlExportException> {
            reportControlExportService.generate(partnerReport, PARTNER_ID, REPORT_ID, pluginKey)
        }

    }
}
