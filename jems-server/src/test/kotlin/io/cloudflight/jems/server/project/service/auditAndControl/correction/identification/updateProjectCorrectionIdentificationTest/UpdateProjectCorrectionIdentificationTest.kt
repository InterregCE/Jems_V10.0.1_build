package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.updateProjectCorrectionIdentificationTest

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProviderTest
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.AuditControlNotOngoingException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.closeProjectAuditCorrection.PartnerOrReportOrFundNotSelectedException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.ProjectCorrectionIdentificationPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.updateCorrectionIdentification.UpdateProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentification
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionIdentificationUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.correction.updateProjectAuditCorrection.CorrectionIsInStatusClosedException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.updateProjectAuditCorrection.PartnerReportNotValidException
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.ZonedDateTime

class UpdateProjectCorrectionIdentificationTest : UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 1L
        private const val PARTNER_ID = 1L
        private const val REPORT_ID = 3L
        private const val PROGRAMME_FUND_ID = 11L
        private val zonedDateNow = ZonedDateTime.now()
        private val zonedDateTomorrow = ZonedDateTime.now().plusDays(1)

        private fun correction(status: CorrectionStatus) = ProjectAuditControlCorrection(
            id = CORRECTION_ID,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 10,
            status = status,
            linkedToInvoice = true,
        )

        private fun correctionIdentification(
            partnerId: Long?,
            reportId: Long?,
            programmeFundId: Long?,
            status: CorrectionStatus
        ) = ProjectCorrectionIdentification(
            correction = correction(status),
            followUpOfCorrectionId = CORRECTION_ID,
            correctionFollowUpType = CorrectionFollowUpType.No,
            repaymentFrom = zonedDateNow,
            lateRepaymentTo = zonedDateTomorrow,
            partnerId = partnerId,
            partnerReportId = reportId,
            programmeFundId = programmeFundId
        )

        private fun correctionIdentificationUpdate(partnerId: Long?, reportId: Long?, programmeFundId: Long?) =
            ProjectCorrectionIdentificationUpdate(
                followUpOfCorrectionId = CORRECTION_ID,
                correctionFollowUpType = CorrectionFollowUpType.No,
                repaymentFrom = zonedDateNow,
                lateRepaymentTo = zonedDateTomorrow,
                partnerId = partnerId,
                partnerReportId = reportId,
                programmeFundId = programmeFundId
            )

        private fun auditControl(status: AuditStatus) = ProjectAuditControl(
            id = 1L,
            number = 1,
            projectId = AuditControlPersistenceProviderTest.PROJECT_ID,
            projectCustomIdentifier = "01",
            status = status,
            controllingBody = ControllingBody.OLAF,
            controlType = AuditControlType.Administrative,
            startDate = AuditControlPersistenceProviderTest.DATE.minusDays(1),
            endDate = AuditControlPersistenceProviderTest.DATE.plusDays(1),
            finalReportDate = AuditControlPersistenceProviderTest.DATE.minusDays(5),
            totalControlledAmount = BigDecimal.valueOf(10000),
            totalCorrectionsAmount = BigDecimal.ZERO,
            comment = null
        )

        private fun partnerReport(status: ReportStatus) = ProjectPartnerReportSubmissionSummary(
            id = 42L,
            reportNumber = 7,
            status = status,
            version = "5.6.1",
            // not important
            firstSubmission = ZonedDateTime.now(),
            controlEnd = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "FG01_654",
            projectAcronym = "acronym",
            partnerAbbreviation = "LP-1",
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerId = 1L
        )

    }

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @MockK
    lateinit var correctionIdentificationPersistence: ProjectCorrectionIdentificationPersistence

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    lateinit var partnerReportPersistence: ProjectPartnerReportPersistence

    @InjectMockKs
    lateinit var updateProjectCorrectionIdentification: UpdateProjectCorrectionIdentification

    @Test
    fun updateCorrectionIdentificationTest() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns auditControl(
            AuditStatus.Ongoing
        )
        every { correctionPersistence.getByCorrectionId(CORRECTION_ID) } returns correction(CorrectionStatus.Ongoing)
        every { partnerReportPersistence.getPartnerReportByIdUnsecured(REPORT_ID) } returns partnerReport(ReportStatus.Certified)
        every {
            correctionIdentificationPersistence.updateCorrectionIdentification(
                CORRECTION_ID, correctionIdentificationUpdate(PARTNER_ID, REPORT_ID, PROGRAMME_FUND_ID)
            )
        } returns correctionIdentification(1L, REPORT_ID, PROGRAMME_FUND_ID, CorrectionStatus.Ongoing)

        assertThat(
            updateProjectCorrectionIdentification.updateProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID,
                correctionIdentificationUpdate(PARTNER_ID, REPORT_ID, PROGRAMME_FUND_ID)
            )
        ).isEqualTo(correctionIdentification(1L, REPORT_ID, PROGRAMME_FUND_ID, CorrectionStatus.Ongoing))
    }

    @Test
    fun `updateCorrectionIdentificationTest - PartnerReport not selected exception`() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns auditControl(
            AuditStatus.Ongoing
        )
        every { correctionPersistence.getByCorrectionId(CORRECTION_ID) } returns correction(CorrectionStatus.Ongoing)
        every { partnerReportPersistence.getPartnerReportByIdUnsecured(REPORT_ID) } returns partnerReport(ReportStatus.Certified)
        every {
            correctionIdentificationPersistence.updateCorrectionIdentification(
                CORRECTION_ID, correctionIdentificationUpdate(PARTNER_ID, REPORT_ID, PROGRAMME_FUND_ID)
            )
        } returns correctionIdentification(null, null, null, CorrectionStatus.Ongoing)

        assertThrows<PartnerOrReportOrFundNotSelectedException> {
            updateProjectCorrectionIdentification.updateProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID,
                correctionIdentificationUpdate(null, null, null)
            )
        }
    }

    @Test
    fun `updateCorrectionIdentificationTest - PartnerReport in wrong status exception`() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns auditControl(
            AuditStatus.Ongoing
        )
        every { correctionPersistence.getByCorrectionId(CORRECTION_ID) } returns correction(CorrectionStatus.Ongoing)
        every { partnerReportPersistence.getPartnerReportByIdUnsecured(REPORT_ID) } returns partnerReport(ReportStatus.Submitted).copy(controlEnd = null)
        every {
            correctionIdentificationPersistence.updateCorrectionIdentification(
                CORRECTION_ID, correctionIdentificationUpdate(PARTNER_ID, REPORT_ID, PROGRAMME_FUND_ID)
            )
        } returns correctionIdentification(null, null, null, CorrectionStatus.Ongoing)

        assertThrows<PartnerReportNotValidException> {
            updateProjectCorrectionIdentification.updateProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID,
                correctionIdentificationUpdate(PARTNER_ID, REPORT_ID, PROGRAMME_FUND_ID)
            )
        }
    }

    @Test
    fun `updateCorrectionIdentificationTest - audit control is closed exception`() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns auditControl(
            AuditStatus.Closed
        )
        every { correctionPersistence.getByCorrectionId(CORRECTION_ID) } returns correction(CorrectionStatus.Ongoing)
        every { partnerReportPersistence.getPartnerReportByIdUnsecured(REPORT_ID) } returns partnerReport(ReportStatus.Certified)
        every {
            correctionIdentificationPersistence.updateCorrectionIdentification(
                CORRECTION_ID, correctionIdentificationUpdate(PARTNER_ID, REPORT_ID, PROGRAMME_FUND_ID)
            )
        } returns correctionIdentification(1L, REPORT_ID, PROGRAMME_FUND_ID, CorrectionStatus.Ongoing)

        assertThrows<AuditControlNotOngoingException> {
            updateProjectCorrectionIdentification.updateProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID,
                correctionIdentificationUpdate(PARTNER_ID, REPORT_ID, PROGRAMME_FUND_ID)
            )
        }
    }

    @Test
    fun `updateCorrectionIdentificationTest - correction is closed exception`() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns auditControl(
            AuditStatus.Ongoing
        )
        every { correctionPersistence.getByCorrectionId(CORRECTION_ID) } returns correction(CorrectionStatus.Closed)
        every { partnerReportPersistence.getPartnerReportByIdUnsecured(REPORT_ID) } returns partnerReport(ReportStatus.Certified)
        every {
            correctionIdentificationPersistence.updateCorrectionIdentification(
                CORRECTION_ID, correctionIdentificationUpdate(PARTNER_ID, REPORT_ID, PROGRAMME_FUND_ID)
            )
        } returns correctionIdentification(1L, REPORT_ID, PROGRAMME_FUND_ID, CorrectionStatus.Closed)

        assertThrows<CorrectionIsInStatusClosedException> {
            updateProjectCorrectionIdentification.updateProjectAuditCorrection(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                CORRECTION_ID,
                correctionIdentificationUpdate(PARTNER_ID, REPORT_ID, PROGRAMME_FUND_ID)
            )
        }
    }
}
