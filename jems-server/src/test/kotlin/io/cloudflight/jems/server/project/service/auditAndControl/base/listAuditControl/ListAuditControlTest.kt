package io.cloudflight.jems.server.project.service.auditAndControl.base.listAuditControl

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ListAuditControlTest: UnitTest() {

    @MockK
    private lateinit var auditControlPersistence: AuditControlPersistenceProvider

    @InjectMockKs
    private lateinit var interactor: ListAuditControl

    @Test
    fun listProjectAudits() {
        val result = mockk<Page<AuditControl>>()
        every { auditControlPersistence.findAllProjectAudits(7L, Pageable.unpaged()) } returns result
        assertThat(interactor.listForProject(7L, Pageable.unpaged())).isEqualTo(result)
    }

}
