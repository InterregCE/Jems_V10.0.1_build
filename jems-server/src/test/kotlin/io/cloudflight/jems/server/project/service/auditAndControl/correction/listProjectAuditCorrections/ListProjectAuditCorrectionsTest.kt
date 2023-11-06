package io.cloudflight.jems.server.project.service.auditAndControl.correction.listProjectAuditCorrections

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.listProjectAuditCorrection.ListProjectAuditControlCorrections
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionStatus
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit.UpdateProjectAuditTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class ListProjectAuditCorrectionsTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 1L
        private const val PROJECT_ID = 2L
        private const val CORRECTION_ID = 3L

        private val correction = ProjectAuditControlCorrection(
            id = CORRECTION_ID,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 1,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
        )

        private val projectAuditControl =  ProjectAuditControl(
            id = AUDIT_CONTROL_ID,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "01",
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

        val expectedCorrectionLine = ProjectAuditControlCorrectionLine(
            id = CORRECTION_ID,
            auditControlId = AUDIT_CONTROL_ID,
            orderNr = 1,
            status = CorrectionStatus.Ongoing,
            linkedToInvoice = true,
            auditControlNumber = 1,
            canBeDeleted = true
        )


    }

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @InjectMockKs
    lateinit var listProjectAuditControlCorrections: ListProjectAuditControlCorrections

    @Test
    fun listProjectAuditCorrections() {
        every { auditControlPersistence.getByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns projectAuditControl
        every { correctionPersistence.getAllCorrectionsByAuditControlId(
            AUDIT_CONTROL_ID,
            Pageable.unpaged()
        ) } returns PageImpl(listOf(correction))
        every { correctionPersistence.getLastCorrectionOngoingId(AUDIT_CONTROL_ID) } returns CORRECTION_ID

        assertThat(
            listProjectAuditControlCorrections.listProjectAuditCorrections(
                PROJECT_ID,
                AUDIT_CONTROL_ID,
                Pageable.unpaged()
            ).content
        ).isEqualTo(listOf(expectedCorrectionLine))
    }
}
