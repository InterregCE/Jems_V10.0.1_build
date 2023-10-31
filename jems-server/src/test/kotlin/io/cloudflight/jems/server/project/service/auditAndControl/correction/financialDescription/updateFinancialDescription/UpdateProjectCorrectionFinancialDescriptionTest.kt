package io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.updateFinancialDescription

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.ProjectCorrectionFinancialDescriptionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.updateProjectCorrectionFinancialDescription.AuditControlIsInStatusClosedException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.updateProjectCorrectionFinancialDescription.CorrectionIsInStatusClosedException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.financialDescription.updateProjectCorrectionFinancialDescription.UpdateProjectProjectCorrectionFinancialDescription
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.*
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
        private const val PROJECT_ID = 5L
        private const val CONTROL_ID = 4L
        private const val CORRECTION_ID = 3L

        private val financialDescription = ProjectCorrectionFinancialDescription(
            correctionId =  CORRECTION_ID,
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
    lateinit var financialDescriptionPersistence: ProjectCorrectionFinancialDescriptionPersistence

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @InjectMockKs
    lateinit var updateProjectCorrectionFinancialDescription: UpdateProjectProjectCorrectionFinancialDescription

    @Test
    fun updateCorrectionFinancialDescription() {
        val control = mockk<ProjectAuditControl>()
        every { control.status } returns AuditStatus.Ongoing

        val correction = mockk<ProjectAuditControlCorrection>()
        every { correction.status } returns CorrectionStatus.Ongoing

        every { financialDescriptionPersistence.updateCorrectionFinancialDescription(
            CORRECTION_ID, financialDescriptionUpdate
        )} returns financialDescription

        every { auditControlPersistence.getByIdAndProjectId(CONTROL_ID, PROJECT_ID) } returns control
        every { correctionPersistence.getByCorrectionId(CORRECTION_ID) } returns correction

        assertThat(
            updateProjectCorrectionFinancialDescription.updateCorrectionFinancialDescription(
                PROJECT_ID,
                CONTROL_ID,
                CORRECTION_ID,
                financialDescriptionUpdate
            )
        ).isEqualTo(financialDescription)
    }

    @Test
    fun `updateCorrectionFinancialDescription - audit control is closed exception`() {
        val control = mockk<ProjectAuditControl>()
        every { control.status } returns AuditStatus.Closed

        every { financialDescriptionPersistence.updateCorrectionFinancialDescription(
            CORRECTION_ID, financialDescriptionUpdate
        )} returns financialDescription

        every { auditControlPersistence.getByIdAndProjectId(CONTROL_ID, PROJECT_ID) } returns control

        assertThrows<AuditControlIsInStatusClosedException> {
            updateProjectCorrectionFinancialDescription.updateCorrectionFinancialDescription(
                PROJECT_ID,
                CONTROL_ID,
                CORRECTION_ID,
                financialDescriptionUpdate
            )
        }
    }

    @Test
    fun `updateCorrectionFinancialDescription - correction is closed exception`() {
        val control = mockk<ProjectAuditControl>()
        every { control.status } returns AuditStatus.Ongoing

        val correction = mockk<ProjectAuditControlCorrection>()
        every { correction.status } returns CorrectionStatus.Closed

        every { financialDescriptionPersistence.updateCorrectionFinancialDescription(
            CORRECTION_ID, financialDescriptionUpdate
        )} returns financialDescription

        every { auditControlPersistence.getByIdAndProjectId(CONTROL_ID, PROJECT_ID) } returns control
        every { correctionPersistence.getByCorrectionId(CORRECTION_ID) } returns correction

        assertThrows<CorrectionIsInStatusClosedException> {
            updateProjectCorrectionFinancialDescription.updateCorrectionFinancialDescription(
                PROJECT_ID,
                CONTROL_ID,
                CORRECTION_ID,
                financialDescriptionUpdate
            )
        }
    }
}
