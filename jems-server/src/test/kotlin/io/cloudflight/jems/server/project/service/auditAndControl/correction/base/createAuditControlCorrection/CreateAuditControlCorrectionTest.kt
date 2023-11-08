package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.createAuditControlCorrection

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCreateCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionFollowUpType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

class CreateAuditControlCorrectionTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L

        private fun projectAuditControl(auditStatus: AuditControlStatus) =  AuditControl(
            id = AUDIT_CONTROL_ID,
            number = 9,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "ID_02",
            projectAcronym = "Acr 02 Proj",
            status = auditStatus,
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.Administrative,
            startDate = mockk(),
            endDate = mockk(),
            finalReportDate = mockk(),
            totalControlledAmount = mockk(),
            totalCorrectionsAmount = mockk(),
            comment = "dumm comment 02",
        )

        private val expectedCreate = AuditControlCorrectionCreate(
            orderNr = 8,
            status = AuditControlStatus.Ongoing,
            type = AuditControlCorrectionType.LinkedToInvoice,
            followUpOfCorrectionType = CorrectionFollowUpType.No,
        )

    }

    @MockK
    private lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    private lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @MockK
    private lateinit var createCorrectionPersistence: AuditControlCreateCorrectionPersistence

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var createProjectAuditControlCorrection: CreateAuditControlCorrection

    @Test
    fun createCorrection() {
        every { auditControlPersistence.getById(AUDIT_CONTROL_ID) } returns projectAuditControl(AuditControlStatus.Ongoing)
        every { auditControlCorrectionPersistence.getLastUsedOrderNr(AUDIT_CONTROL_ID) } returns 7

        val result = mockk<AuditControlCorrectionDetail>()
        every { result.orderNr } returns 8
        val slotToCreate = slot<AuditControlCorrectionCreate>()
        every { createCorrectionPersistence.createCorrection(AUDIT_CONTROL_ID, capture(slotToCreate)) } returns result

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers { }

        assertThat(createProjectAuditControlCorrection.createCorrection(AUDIT_CONTROL_ID, AuditControlCorrectionType.LinkedToInvoice))
            .isEqualTo(result)

        assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CORRECTION_IS_CREATED,
                project = AuditProject("2", customIdentifier = "ID_02", name = "Acr 02 Proj"),
                description = "Correction AC9.8 for Audit/Control number ID_02_AC_9 is created."
            )
        )
        assertThat(slotToCreate.captured).isEqualTo(expectedCreate)
    }

    @Test
    fun `createCorrection - maximum number of corrections exception`() {
        every { auditControlPersistence.getById(18) } returns projectAuditControl(AuditControlStatus.Ongoing)
        every { auditControlCorrectionPersistence.getLastUsedOrderNr(18) } returns 100

        assertThrows<MaximumNumberOfCorrectionsException> {
            createProjectAuditControlCorrection.createCorrection(18, AuditControlCorrectionType.LinkedToCostOption)
        }
    }

    @Test
    fun `createProjectAuditCorrection - audit control not ongoing exception`() {
        every { auditControlPersistence.getById(19) } returns projectAuditControl(AuditControlStatus.Closed)

        assertThrows<AuditControlClosedException> {
            createProjectAuditControlCorrection.createCorrection(19, AuditControlCorrectionType.LinkedToInvoice)
        }
    }
}
