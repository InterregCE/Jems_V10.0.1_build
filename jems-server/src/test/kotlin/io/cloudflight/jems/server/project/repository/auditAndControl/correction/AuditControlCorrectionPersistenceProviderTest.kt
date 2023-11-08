package io.cloudflight.jems.server.project.repository.auditAndControl.correction

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuditControlCorrectionPersistenceProviderTest : UnitTest() {

    /*
    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 1L

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

        private val projectSummary = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "test",
            callId = 1L,
            callName = "call",
            acronym = "test",
            status = ApplicationStatus.CONTRACTED,
        )

        private val correction = ProjectAuditControlCorrection(
            id = 1L,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 10,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
        )

        private val extendedCorrection = ProjectAuditControlCorrectionExtended(
            correction = correction, auditControlNumber = 20, projectCustomIdentifier = projectSummary.customIdentifier
        )

        private val correctionEntity = AuditControlCorrectionEntity(
            id = 1L,
            auditControlEntity = projectAuditControlEntity,
            orderNr = 10,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
        )
    }
    */

    @MockK
    private lateinit var auditControlCorrectionRepository: AuditControlCorrectionRepository
    @MockK
    private lateinit var partnerReportRepository: ProjectPartnerReportRepository
    @MockK
    private lateinit var programmeFundRepository: ProgrammeFundRepository

    @InjectMockKs
    lateinit var persistence: AuditControlCorrectionPersistenceProvider

    @Test
    fun getProjectIdForCorrection() {
        val entity = mockk<AuditControlCorrectionEntity>()
        every { entity.auditControl.project.id } returns 45L
        every { auditControlCorrectionRepository.getById(256L) } returns entity

        assertThat(persistence.getProjectIdForCorrection(256L))
            .isEqualTo(45L)
    }

    /*
    @Test
    fun getAllCorrectionsByAuditControlId() {
        every {
            auditControlCorrectionRepository.findAllByAuditControlEntityId(
                AUDIT_CONTROL_ID, Pageable.unpaged()
            )
        } returns PageImpl(listOf(correctionEntity))

        assertThat(
            auditControlCorrectionPersistenceProvider.getAllCorrectionsByAuditControlId(
                AUDIT_CONTROL_ID, Pageable.unpaged()
            ).content
        ).isEqualTo(listOf(correction))
    }

    @Test
    fun getByCorrectionId() {
        every { auditControlCorrectionRepository.getById(CORRECTION_ID) } returns correctionEntity

        assertThat(auditControlCorrectionPersistenceProvider.getByCorrectionId(CORRECTION_ID)).isEqualTo(correction)
    }

    @Test
    fun getExtendedByCorrectionId() {
        every { auditControlCorrectionRepository.getById(CORRECTION_ID) } returns correctionEntity

        assertThat(auditControlCorrectionPersistenceProvider.getExtendedByCorrectionId(CORRECTION_ID)).isEqualTo(
            extendedCorrection
        )
    }

    @Test
    fun getLastUsedOrderNr() {
        every { auditControlCorrectionRepository.findFirstByAuditControlEntityIdOrderByOrderNrDesc(AUDIT_CONTROL_ID) } returns correctionEntity

        assertThat(auditControlCorrectionPersistenceProvider.getLastUsedOrderNr(AUDIT_CONTROL_ID)).isEqualTo(10)
    }

    @Test
    fun getLastCorrectionIdByAuditControlId() {
        every {
            auditControlCorrectionRepository.getFirstByAuditControlEntityIdAndStatusOrderByOrderNrDesc(
                AUDIT_CONTROL_ID, CorrectionStatus.Ongoing
            )
        } returns correctionEntity

        assertThat(auditControlCorrectionPersistenceProvider.getLastCorrectionOngoingId(AUDIT_CONTROL_ID)).isEqualTo(1)
    }
*/
}
