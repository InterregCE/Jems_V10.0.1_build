package io.cloudflight.jems.server.project.service.report.partner.base.deleteProjectPartnerReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher

internal class DeleteProjectPartnerReportTest: UnitTest()  {

    companion object {
        private const val PROJECT_ID = 500L
        private const val PARTNER_ID = 420L
        private const val REPORT_ID = 10L
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var interactor: DeleteProjectPartnerReport

    @BeforeEach
    fun reset() {
        clearMocks(partnerPersistence, reportPersistence, auditPublisher)
    }

    @ParameterizedTest(name = "delete successfully")
    @EnumSource(value = ReportStatus::class, names = ["Draft"])
    fun delete(status: ReportStatus) {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, REPORT_ID) } returns report(status)
        every { reportPersistence.getCurrentLatestReportNumberForPartner(PARTNER_ID) } returns 5
        every { reportPersistence.deletePartnerReportById(REPORT_ID) } answers { }
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { auditPublisher.publishEvent(any()) } answers { }

        val slotAudit = slot<AuditCandidateEvent>()

        interactor.delete(PARTNER_ID, REPORT_ID)
        verify(exactly = 1) { reportPersistence.deletePartnerReportById(REPORT_ID) }
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        Assertions.assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PARTNER_REPORT_DELETED,
                entityRelatedId = 100,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "identifier", name = "acronym"),
                description = "[identifier] [PP420] Draft partner report R.5 deleted"
            )
        )
    }

    @Test
    fun `delete - invalid report because it is not most recent`() {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, REPORT_ID) } returns report(ReportStatus.Draft)
        every { reportPersistence.getCurrentLatestReportNumberForPartner(PARTNER_ID) } returns 6
        every { reportPersistence.deletePartnerReportById(REPORT_ID) } answers { }
        assertThrows<DeletionIsNotAllowedException> { interactor.delete(PARTNER_ID, REPORT_ID) }
        verify(exactly = 0) { reportPersistence.deletePartnerReportById(REPORT_ID) }
    }

    @ParameterizedTest(name = "delete - invalid report because of wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Draft"], mode = EnumSource.Mode.EXCLUDE)
    fun `delete - invalid report because status not equals to draft`(status: ReportStatus) {
        every { reportPersistence.getPartnerReportById(PARTNER_ID, REPORT_ID) } returns report(status)
        every { reportPersistence.getCurrentLatestReportNumberForPartner(PARTNER_ID) } returns 5
        every { reportPersistence.deletePartnerReportById(REPORT_ID) } answers { }
        assertThrows<DeletionIsNotAllowedException> { interactor.delete(PARTNER_ID, REPORT_ID) }
        verify(exactly = 0) { reportPersistence.deletePartnerReportById(REPORT_ID) }
    }

    private fun report(status: ReportStatus): ProjectPartnerReport {
        return ProjectPartnerReport(
            id = 100L,
            reportNumber = 5,
            status = status,
            version = "v1.0",
            firstSubmission = null,
            identification = PartnerReportIdentification(
                projectIdentifier = "identifier",
                projectAcronym = "acronym",
                partnerNumber = 420,
                partnerAbbreviation = "sample",
                partnerRole = ProjectPartnerRole.PARTNER,
                coFinancing = listOf(),
                nameInEnglish = null,
                nameInOriginalLanguage = null,
                legalStatus = null,
                partnerType = null,
                vatRecovery = null,
                country = null,
                currency = null
            )
        )
    }
}
