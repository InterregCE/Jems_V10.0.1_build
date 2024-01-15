package io.cloudflight.jems.server.project.service.report.project.verification.certificate.generate

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.project.report.ProjectReportVerificationCertificatePlugin
import io.cloudflight.jems.plugin.contract.models.common.UserSummaryData
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationCertificate
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
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
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.authority.SimpleGrantedAuthority

class GenerateVerificationCertificateTest {

    companion object {
        const val pluginKey = "standard-project-report-verification-certificate-generate-plugin"
        const val PROJECT_ID = 1L
        const val REPORT_ID = 3L

        val JSMA_ROLE = UserRole(id = 7, name = "jsma", permissions = emptySet(), isDefault = false)
        val jsmaUser = User(
            id = 3,
            email = "controller@jems.eu",
            userSettings = UserSettings(sendNotificationsToEmail = false),
            name = "John",
            surname = "Doe",
            userRole = JSMA_ROLE,
            userStatus = UserStatus.ACTIVE
        )
        val localJsmaUser = LocalCurrentUser(
            jsmaUser, "hash_pass", listOf(
                SimpleGrantedAuthority("ROLE_" + JSMA_ROLE.name)
            )
        )

        val projectReport = mockk<ProjectReportModel> {
            every { id } returns 101L
            every { projectId } returns PROJECT_ID
            every { reportNumber } returns 5
            every { projectAcronym } returns "VerCertAcro"
            every { projectIdentifier } returns "VER_CER_ID"
        }
    }

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectReportPersistence: ProjectReportPersistence

    @MockK
    lateinit var projectReportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var getLogosInteractor: GetLogosInteractor

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var projectReportVerificationCertificatePlugin: ProjectReportVerificationCertificatePlugin

    @InjectMockKs
    lateinit var interactor: GenerateVerificationCertificate

    @BeforeEach
    fun setup() {
        clearMocks(
            jemsPluginRegistry,
            securityService,
            projectReportPersistence,
            projectReportFilePersistence,
            getLogosInteractor,
            auditPublisher,
        )

        every { securityService.currentUser } returns localJsmaUser
        every { securityService.getUserIdOrThrow() } returns localJsmaUser.user.id
    }

    @Test
    fun generateCertificate() {
        val exportResult = ExportResult("pdf", "Verification Certificate - VER_CER_ID - PR 5.pdf", byteArrayOf())
        val currentUserSummaryData = UserSummaryData(
            id = jsmaUser.id,
            email = jsmaUser.email,
            name = jsmaUser.name,
            surname = jsmaUser.surname
        )
        val identifier = 101L

        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns projectReport
        every { jemsPluginRegistry.get(ProjectReportVerificationCertificatePlugin::class, pluginKey) } returns projectReportVerificationCertificatePlugin
        every { projectReportFilePersistence.countProjectReportVerificationCertificates(PROJECT_ID, REPORT_ID) } returns identifier - 1
        every { getLogosInteractor.getLogos() } returns listOf()

        every {
            projectReportVerificationCertificatePlugin.generateCertificate(
                projectId = PROJECT_ID,
                reportId = REPORT_ID,
                logo = null,
                creationDate = any(),
                currentUser = currentUserSummaryData
            )
        } returns exportResult

        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns mockk()


        val slot = slot<JemsFileCreate>()
        every { projectReportFilePersistence.saveVerificationCertificateFile(capture(slot)) } returns mockk()

        interactor.generateCertificate(PROJECT_ID, REPORT_ID, pluginKey)

        verify(exactly = 1) { projectReportFilePersistence.saveVerificationCertificateFile(slot.captured) }
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        Assertions.assertThat(slotAudit.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_REPORT_VERIFICATION_CERTIFICATE_GENERATED)
        Assertions.assertThat(slotAudit.captured.auditCandidate.description)
            .isEqualTo("A verification certificate was generated for project report PR.5")

        Assertions.assertThat(slot.captured.type).isEqualTo(VerificationCertificate)
        Assertions.assertThat(slot.captured.name).matches("Verification Certificate $identifier - VER_CER_ID - PR 5.pdf")
    }
}
