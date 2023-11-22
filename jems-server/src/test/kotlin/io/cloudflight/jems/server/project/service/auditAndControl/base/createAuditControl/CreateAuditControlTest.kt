package io.cloudflight.jems.server.project.service.auditAndControl.base.createAuditControl

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlCreate
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.validator.ProjectAuditAndControlValidator
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
import java.time.ZonedDateTime

class CreateAuditControlTest: UnitTest() {

    private val start = ZonedDateTime.now().minusDays(1)
    private val end = ZonedDateTime.now().plusDays(1)
    private val final = ZonedDateTime.now().plusWeeks(1)

    @MockK
    private lateinit var auditControlPersistence: AuditControlPersistenceProvider

    @MockK
    private lateinit var projectAuditAndControlValidator: ProjectAuditAndControlValidator

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var interactor: CreateAuditControl

    @BeforeEach
    fun reset() {
        clearMocks(auditPublisher)
    }

    @Test
    fun createAudit() {
        every { auditControlPersistence.countAuditsForProject(45L) } returns 99
        val toCreateData = AuditControlUpdate(
            controllingBody = ControllingBody.RegionalApprobationBody,
            controlType = AuditControlType.Administrative,
            startDate = start,
            endDate = end,
            finalReportDate = final,
            totalControlledAmount = BigDecimal.valueOf(745L),
            comment = "dummy comment",
        )
        every { projectAuditAndControlValidator.validateData(toCreateData) } answers { }

        val slotToCreate = slot<AuditControlCreate>()
        val result = mockk<AuditControl> {
            every { id } returns 11L
            every { projectId } returns 24L
            every { projectCustomIdentifier } returns "ID-24"
            every { projectAcronym } returns "24-Acr"
            every { number } returns 5
        }
        every { auditControlPersistence.createControl(45L, capture(slotToCreate)) } returns result

        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } answers { }

        assertThat(interactor.createAudit(45L, toCreateData)).isEqualTo(result)

        assertThat(slotToCreate.captured).isEqualTo(AuditControlCreate(
            number = 100,
            status = AuditControlStatus.Ongoing,
            controllingBody = ControllingBody.RegionalApprobationBody,
            controlType = AuditControlType.Administrative,
            startDate = start,
            endDate = end,
            finalReportDate = final,
            totalControlledAmount = BigDecimal.valueOf(745L),
            comment = "dummy comment",
        ))

        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_AUDIT_CONTROL_IS_CREATED,
                project = AuditProject("24", "ID-24", "24-Acr"),
                entityRelatedId = 11L,
                description = "Audit/Control ID-24_AC_5 is created",
            )
        )
    }

    @Test
    fun `createAudit - max amount reached`() {
        every { auditControlPersistence.countAuditsForProject(48L) } returns 100
        assertThrows<MaxNumberOfAuditsReachedException> { interactor.createAudit(48L, mockk()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

}
