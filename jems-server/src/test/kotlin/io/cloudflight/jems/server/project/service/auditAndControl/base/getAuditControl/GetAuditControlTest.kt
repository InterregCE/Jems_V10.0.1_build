package io.cloudflight.jems.server.project.service.auditAndControl.base.getAuditControl

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetAuditControlTest: UnitTest() {

    @MockK
    private lateinit var auditControlPersistence: AuditControlPersistence

    @InjectMockKs
    private lateinit var interactor: GetAuditControl

    @Test
    fun getProjectAuditDetails() {
        val result = mockk<AuditControl>()
        every { auditControlPersistence.getById(auditControlId = 65L) } returns result
        assertThat(interactor.getDetails(auditControlId = 65L)).isEqualTo(result)
    }

}
