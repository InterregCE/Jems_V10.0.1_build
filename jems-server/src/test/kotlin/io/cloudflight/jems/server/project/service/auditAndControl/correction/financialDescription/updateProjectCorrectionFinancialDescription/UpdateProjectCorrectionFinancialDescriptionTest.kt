package io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.updateProjectCorrectionFinancialDescription

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.AuditControlCorrectionFinancePersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.*
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class UpdateProjectCorrectionFinancialDescriptionTest: UnitTest() {

    companion object {
        private const val CONTROL_ID = 4L
        private const val CORRECTION_ID = 3L

        private val financialDescriptionUpdate = ProjectCorrectionFinancialDescriptionUpdate(
            deduction = true,
            fundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ONE,
            privateContribution = BigDecimal.ZERO,
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "sample comment",
            correctionType = CorrectionType.Ref1Dot15,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = null
        )
    }

    @MockK
    lateinit var financialDescriptionPersistence: AuditControlCorrectionFinancePersistence

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @InjectMockKs
    lateinit var updateProjectCorrectionFinancialDescription: UpdateProjectProjectCorrectionFinancialDescription

    @Test
    fun updateCorrectionFinancialDescription() {
        val control = mockk<AuditControl>()
        every { control.status } returns AuditControlStatus.Ongoing

        val correction = mockk<AuditControlCorrectionDetail>()
        every { correction.status } returns AuditControlStatus.Ongoing
        every { correction.auditControlId } returns CONTROL_ID

        val result = mockk<ProjectCorrectionFinancialDescription>()
        every { financialDescriptionPersistence.updateCorrectionFinancialDescription(
            CORRECTION_ID, financialDescriptionUpdate
        )} returns result

        every { auditControlPersistence.getById(CONTROL_ID) } returns control
        every { correctionPersistence.getByCorrectionId(CORRECTION_ID) } returns correction

        assertThat(
            updateProjectCorrectionFinancialDescription.updateCorrectionFinancialDescription(
                CORRECTION_ID,
                financialDescriptionUpdate
            )
        ).isEqualTo(result)
    }

    @Test
    fun `updateCorrectionFinancialDescription - audit control is closed exception`() {
        every { correctionPersistence.getByCorrectionId(16L) } returns
                mockk {
                    every { auditControlId } returns 475L
                    every { status } returns AuditControlStatus.Ongoing
                }
        every { auditControlPersistence.getById(475L) } returns
                mockk { every { status } returns AuditControlStatus.Closed }

        assertThrows<AuditControlIsInStatusClosedException> {
            updateProjectCorrectionFinancialDescription.updateCorrectionFinancialDescription(16L, mockk())
        }
    }

    @Test
    fun `updateCorrectionFinancialDescription - correction is closed exception`() {
        every { correctionPersistence.getByCorrectionId(17L) } returns
                mockk {
                    every { auditControlId } returns 477L
                    every { status } returns AuditControlStatus.Closed
                }
        every { auditControlPersistence.getById(477L) } returns
                mockk { every { status } returns AuditControlStatus.Ongoing }

        assertThrows<CorrectionIsInStatusClosedException> {
            updateProjectCorrectionFinancialDescription.updateCorrectionFinancialDescription(17L, mockk())
        }
    }
}
