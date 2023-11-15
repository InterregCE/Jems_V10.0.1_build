package io.cloudflight.jems.server.project.service.auditAndControl.correction.impact.updateImpact

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.impact.AuditControlCorrectionImpactPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.*
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.CorrectionImpactAction
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateAuditControlCorrectionImpactTest: UnitTest() {

    companion object {
        private const val CONTROL_ID = 4L
        private const val CORRECTION_ID = 3L

        private val toUpdate = AuditControlCorrectionImpact(
            action = CorrectionImpactAction.RepaymentByProject,
            comment = "impact comment",
        )
    }

    @MockK
    private lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    private lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @MockK
    private lateinit var impactPersistence: AuditControlCorrectionImpactPersistence

    @InjectMockKs
    private lateinit var interactor: UpdateAuditControlCorrectionImpact

    @Test
    fun updateCorrectionFinancialDescription() {
        val control = mockk<AuditControl>()
        every { control.status } returns AuditControlStatus.Ongoing

        val correction = mockk<AuditControlCorrectionDetail>()
        every { correction.status } returns AuditControlStatus.Ongoing
        every { correction.auditControlId } returns CONTROL_ID

        every { impactPersistence.updateCorrectionImpact(CORRECTION_ID, toUpdate)} returnsArgument 1

        every { auditControlPersistence.getById(CONTROL_ID) } returns control
        every { auditControlCorrectionPersistence.getByCorrectionId(CORRECTION_ID) } returns correction

        assertThat(interactor.update(CORRECTION_ID, toUpdate)).isEqualTo(toUpdate)
    }

    @Test
    fun `update - audit control is closed exception`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(16L) } returns
                mockk {
                    every { auditControlId } returns 475L
                    every { status } returns AuditControlStatus.Ongoing
                }
        every { auditControlPersistence.getById(475L) } returns
                mockk { every { status } returns AuditControlStatus.Closed }

        assertThrows<AuditControlClosedException> {
            interactor.update(16L, mockk())
        }
    }

    @Test
    fun `update - correction is closed exception`() {
        every { auditControlCorrectionPersistence.getByCorrectionId(17L) } returns
                mockk {
                    every { auditControlId } returns 477L
                    every { status } returns AuditControlStatus.Closed
                }
        every { auditControlPersistence.getById(477L) } returns
                mockk { every { status } returns AuditControlStatus.Ongoing }

        assertThrows<AuditControlCorrectionClosedException> {
            interactor.update(17L, mockk())
        }
    }
}
