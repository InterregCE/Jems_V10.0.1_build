package io.cloudflight.jems.server.project.repository.auditAndControl

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionIdentificationEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.identification.CorrectionIdentificationRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.identification.ProjectCorrectionIdentificationPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentificationUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit.UpdateProjectAuditTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class AuditControlCorrectionIdentificationPersistenceTest: UnitTest() {

    @MockK
    lateinit var correctionIdentificationRepository: CorrectionIdentificationRepository

    @InjectMockKs
    lateinit var persistence: ProjectCorrectionIdentificationPersistenceProvider

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 1L
        private const val CORRECTION_ID_2 = 2L
        private const val PROGRAMME_FUND_ID = 11L
        private const val PARTNER_ID = 21L
        private const val PARTNER_REPORT_ID = 31L
        private val zonedDateNow = ZonedDateTime.now()
        private val zonedDateTomorrow = ZonedDateTime.now().plusDays(1)
        private val updatedZonedDateTime = zonedDateNow.plusDays(3)

        private val projectAuditControlEntity = AuditControlEntity(
            id = AUDIT_CONTROL_ID,
            number = 20,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "test",
            status = AuditStatus.Ongoing,
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.Administrative,
            startDate = UpdateProjectAuditTest.DATE.minusDays(1),
            endDate = UpdateProjectAuditTest.DATE.plusDays(1),
            finalReportDate = UpdateProjectAuditTest.DATE.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(10000),
            totalCorrectionsAmount = BigDecimal.ZERO,
            comment = null
        )

        private val correctionEntity = ProjectAuditControlCorrectionEntity(
            id = CORRECTION_ID_2,
            auditControlEntity = projectAuditControlEntity,
            orderNr = 1,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true
        )

        private val correctionIdentificationEntity = ProjectCorrectionIdentificationEntity(
            correctionId = CORRECTION_ID_2,
            correctionEntity = correctionEntity,
            followUpOfCorrectionId = CORRECTION_ID,
            correctionFollowUpType = CorrectionFollowUpType.No,
            repaymentFrom = zonedDateNow,
            lateRepaymentTo = zonedDateTomorrow,
            partnerId = PARTNER_ID,
            partnerReportId = PARTNER_REPORT_ID,
            programmeFundId = PROGRAMME_FUND_ID
        )

        private val correction = ProjectAuditControlCorrection(
            id = CORRECTION_ID_2,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 1,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
        )

        private val expectedCorrectionIdentification = ProjectCorrectionIdentification(
            correction = correction,
            followUpOfCorrectionId = CORRECTION_ID,
            correctionFollowUpType = CorrectionFollowUpType.No,
            repaymentFrom = zonedDateNow,
            lateRepaymentTo = zonedDateTomorrow,
            partnerId = PARTNER_ID,
            partnerReportId = PARTNER_REPORT_ID,
            programmeFundId = PROGRAMME_FUND_ID,
        )

        private val expectedCorrectionIdentificationAfterUpdate = ProjectCorrectionIdentification(
            correction = correction,
            followUpOfCorrectionId = CORRECTION_ID,
            correctionFollowUpType = CorrectionFollowUpType.CourtProcedure,
            repaymentFrom = updatedZonedDateTime,
            lateRepaymentTo = updatedZonedDateTime,
            partnerId = 22L,
            partnerReportId = 32L,
            programmeFundId = 12L,
        )

        private val correctionIdentificationUpdate = ProjectCorrectionIdentificationUpdate(
            followUpOfCorrectionId = CORRECTION_ID,
            correctionFollowUpType = CorrectionFollowUpType.CourtProcedure,
            repaymentFrom = updatedZonedDateTime,
            lateRepaymentTo = updatedZonedDateTime,
            partnerId = 22L,
            partnerReportId = 32L,
            programmeFundId = 12L
        )

    }

    @Test
    fun getCorrectionIdentification() {
        every { correctionIdentificationRepository.getByCorrectionId(CORRECTION_ID) } returns correctionIdentificationEntity

        assertThat(persistence.getCorrectionIdentification(CORRECTION_ID)).isEqualTo(expectedCorrectionIdentification)
    }

    @Test
    fun updateCorrectionIdentification() {
        every { correctionIdentificationRepository.getByCorrectionId(CORRECTION_ID) } returns correctionIdentificationEntity

        assertThat(persistence.updateCorrectionIdentification(CORRECTION_ID, correctionIdentificationUpdate)).isEqualTo(
            expectedCorrectionIdentificationAfterUpdate
        )
    }

}
