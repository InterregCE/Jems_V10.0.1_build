package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ListAuditControlCorrectionTest: UnitTest() {

    companion object {
        val dummyCorrections = listOf(
            AuditControlCorrection(
                id = 541L,
                orderNr = 12,
                status = AuditControlStatus.Ongoing,
                type = AuditControlCorrectionType.LinkedToInvoice,
                auditControlId = 5L,
                auditControlNr = 15,
            ),
            AuditControlCorrection(
                id = 541L,
                orderNr = 12,
                status = AuditControlStatus.Closed,
                type = AuditControlCorrectionType.LinkedToInvoice,
                auditControlId = 5L,
                auditControlNr = 15,
            ),
        )

        val expectedCorrections = listOf(
            AuditControlCorrectionLine(
                id = 541L,
                orderNr = 12,
                status = AuditControlStatus.Ongoing,
                type = AuditControlCorrectionType.LinkedToInvoice,
                auditControlId = 5L,
                auditControlNr = 15,
                canBeDeleted = true,
            ),
            AuditControlCorrectionLine(
                id = 541L,
                orderNr = 12,
                status = AuditControlStatus.Closed,
                type = AuditControlCorrectionType.LinkedToInvoice,
                auditControlId = 5L,
                auditControlNr = 15,
                canBeDeleted = false,
            ),
        )

    }

    @MockK
    private lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    private lateinit var interactor: ListAuditControlCorrection

    @Test
    fun listProjectAuditCorrections() {
        every {
            auditControlCorrectionPersistence.getAllCorrectionsByAuditControlId(15L, Pageable.unpaged())
        } returns PageImpl(dummyCorrections)
        assertThat(interactor.listCorrections(15L, Pageable.unpaged()))
            .containsExactlyElementsOf(expectedCorrections)
    }

}
