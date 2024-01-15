package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.deleteAuditControlCorrection

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
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
import java.math.BigDecimal

class DeleteAuditControlCorrectionTest : UnitTest() {

    private val auditControl = AuditControl(
        id = 475L,
        number = 7,
        projectId = 84L,
        projectCustomIdentifier = "ID_84",
        projectAcronym = "84 Acronym",
        status = AuditControlStatus.Ongoing,
        controllingBody = ControllingBody.GoA,
        controlType = AuditControlType.Administrative,
        startDate = mockk(),
        endDate = mockk(),
        finalReportDate = mockk(),
        totalControlledAmount = BigDecimal.valueOf(75044),
        totalCorrectionsAmount = BigDecimal.valueOf(847500),
        existsOngoing = true,
        existsClosed = true,
        comment = "dummy comment",
    )

    @MockK
    private lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    private lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var deleteProjectAuditControlCorrection: DeleteAuditControlCorrection

    @BeforeEach
    fun setup() {
        clearMocks(auditControlPersistence, auditControlCorrectionPersistence, auditPublisher)
    }

    @Test
    fun deleteCorrection() {
        every { auditControlCorrectionPersistence.getByCorrectionId(14L) } returns
                mockk {
                    every { auditControlId } returns 475L
                    every { status } returns AuditControlStatus.Ongoing
                    every { orderNr } returns 3
                }
        every { auditControlPersistence.getById(475L) } returns auditControl

        every { auditControlCorrectionPersistence.deleteCorrectionById(14L) } answers { }

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        deleteProjectAuditControlCorrection.deleteCorrection(14L)

        verify(exactly = 1) { auditControlCorrectionPersistence.deleteCorrectionById(14L) }

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CORRECTION_IS_DELETED,
                project = AuditProject("84", customIdentifier = "ID_84", name = "84 Acronym"),
                description = "Correction AC7.3 for Audit/Control number ID_84_AC_7 is deleted.",
            )
        )
    }

    @Test
    fun `deleteCorrection - audit control is closed exception`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(14L) } returns
                mockk {
                    every { auditControlId } returns 475L
                    every { status } returns AuditControlStatus.Ongoing
                }
        every { auditControlPersistence.getById(475L) } returns
                mockk { every { status } returns AuditControlStatus.Closed }

        assertThrows<AuditControlClosedException> { deleteProjectAuditControlCorrection.deleteCorrection(14L) }
        verify(exactly = 0) { auditControlCorrectionPersistence.deleteCorrectionById(any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @Test
    fun `deleteCorrection - correction is closed`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(17L) } returns
                mockk {
                    every { auditControlId } returns 310L
                    every { status } returns AuditControlStatus.Closed
                }
        every { auditControlPersistence.getById(310L) } returns
                mockk { every { status } returns AuditControlStatus.Ongoing }

        assertThrows<AuditControlCorrectionClosedException> { deleteProjectAuditControlCorrection.deleteCorrection(17L) }
        verify(exactly = 0) { auditControlCorrectionPersistence.deleteCorrectionById(any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

}
