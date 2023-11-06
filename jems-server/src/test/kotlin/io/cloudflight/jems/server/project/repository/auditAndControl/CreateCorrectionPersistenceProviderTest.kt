package io.cloudflight.jems.server.project.repository.auditAndControl

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionIdentificationEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.CreateCorrectionPersistenceProvider
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.identification.CorrectionIdentificationRepository
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionFollowUpType
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
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

class CreateCorrectionPersistenceProviderTest {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 1L

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

        private val correction = ProjectAuditControlCorrection(
            id = 1,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 1,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
        )

        private val correctionEntity = ProjectAuditControlCorrectionEntity(
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
    }

    @MockK
    lateinit var auditControlCorrectionRepository: AuditControlCorrectionRepository

    @MockK
    lateinit var auditControlRepository: AuditControlRepository

    @MockK
    lateinit var auditControlCorrectionIdentificationRepository: CorrectionIdentificationRepository

    @InjectMockKs
    lateinit var createCorrectionPersistenceProvider: CreateCorrectionPersistenceProvider

    @Test
    fun createCorrection() {
        every { auditControlRepository.getById(AUDIT_CONTROL_ID) } returns projectAuditControlEntity
        every { auditControlCorrectionRepository.save(any()) } returns correctionEntity
        every { auditControlCorrectionIdentificationRepository.save(any()) } returns correctionIdentificationEntity

        assertThat(createCorrectionPersistenceProvider.createCorrection(correction)).isEqualTo(correction)
    }

}
