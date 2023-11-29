package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ListAuditControlCorrectionTest : UnitTest() {

    @MockK
    private lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    private lateinit var interactor: ListAuditControlCorrection

    @Test
    fun listProjectAuditCorrections() {
        val corrections = PageImpl(
            listOf(
                mockk<AuditControlCorrectionLine>(),
                mockk<AuditControlCorrectionLine>(),
            )
        )
        every { auditControlCorrectionPersistence.getAllCorrectionsByAuditControlId(15L, Pageable.unpaged()) } returns corrections

        assertThat(interactor.listCorrections(15L, Pageable.unpaged())).isEqualTo(corrections)
    }

}
