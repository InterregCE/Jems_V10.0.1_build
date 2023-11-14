package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.measure.CorrectionProgrammeMeasureRepository
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test

class AuditControlCreateCorrectionPersistenceProviderTest {

    companion object {
        /*
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 1L
        private val today = LocalDate.now()

        private val projectAuditControlEntity = AuditControlEntity(
            id = AUDIT_CONTROL_ID,
            number = 20,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "test",
            status = AuditControlStatus.Ongoing,
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.Administrative,
            startDate = UpdateProjectAuditTest.DATE.minusDays(1),
            endDate = UpdateProjectAuditTest.DATE.plusDays(1),
            finalReportDate = UpdateProjectAuditTest.DATE.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(10000),
            totalCorrectionsAmount = BigDecimal.ZERO,
            comment = null
        )

        private val correction = ProjectAuditControlCorrection(
            id = 1,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 1,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
        )

        private val correctionEntity = AuditControlCorrectionEntity(
            id = CORRECTION_ID,
            auditControlEntity = projectAuditControlEntity,
            orderNr = 1,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true
        )

        private val correctionIdentificationEntity = ProjectCorrectionIdentificationEntity(
            correctionId = CORRECTION_ID,
            correctionEntity = correctionEntity,
            followUpOfCorrectionId = null,
            correctionFollowUpType = CorrectionFollowUpType.No,
            repaymentFrom = null,
            lateRepaymentTo = null,
            partnerId = null,
            partnerReportId = null,
            programmeFundId = null
        )

        private val financialDescriptionEntity = AuditControlCorrectionFinanceEntity(
            correctionId = CORRECTION_ID,
            correction = correctionEntity,
            deduction = true,
            fundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal(100),
            autoPublicContribution = BigDecimal(101),
            privateContribution = BigDecimal(102),
            infoSentBeneficiaryDate = today,
            infoSentBeneficiaryComment = "BENEFICIARY COMMENT",
            correctionType = CorrectionType.Ref10Dot1,
            clericalTechnicalMistake = true,
            goldPlating = true,
            suspectedFraud = true,
            correctionComment = "CORRECTION COMMENT"
        )

        private val programmeMeasureEntity = AuditControlCorrectionMeasureEntity(
            correctionEntity = correctionEntity,
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5,
            comment = "prog meas",
        )
        */
    }

    @MockK
    private lateinit var auditControlRepository: AuditControlRepository
    @MockK
    private lateinit var auditControlCorrectionRepository: AuditControlCorrectionRepository
    @MockK
    private lateinit var auditControlCorrectionFinanceRepository: ProjectCorrectionFinancialDescriptionRepository
    @MockK
    private lateinit var auditControlCorrectionMeasureRepository: CorrectionProgrammeMeasureRepository

    @InjectMockKs
    lateinit var createCorrectionPersistenceProvider: AuditControlCreateCorrectionPersistenceProvider

    /*
    @Test
    fun createCorrection() {
        every { auditControlRepository.getById(AUDIT_CONTROL_ID) } returns projectAuditControlEntity
        every { auditControlCorrectionRepository.save(any()) } returns correctionEntity
        every { auditControlCorrectionIdentificationRepository.save(any()) } returns correctionIdentificationEntity
        every { projectCorrectionFinancialDescriptionRepository.save(any()) } returns financialDescriptionEntity
        every { programmeMeasureRepository.save(any()) } returns programmeMeasureEntity

        assertThat(createCorrectionPersistenceProvider.createCorrection(correction)).isEqualTo(correction)
    }
    */

}
