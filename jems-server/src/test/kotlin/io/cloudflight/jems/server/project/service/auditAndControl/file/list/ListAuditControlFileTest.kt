package io.cloudflight.jems.server.project.service.auditAndControl.file.list

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ListAuditControlFileTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 13L
    }

    @MockK
    lateinit var listAuditControlFileService: ListAuditControlFileService

    @InjectMockKs
    lateinit var interactor: ListAuditControlFile


    @Test
    fun list() {

        val fileList = mockk<Page<JemsFile>>()
        every { listAuditControlFileService.list(auditControlId = AUDIT_CONTROL_ID, Pageable.unpaged()) } returns fileList

        assertThat(interactor.list(AUDIT_CONTROL_ID, Pageable.unpaged())).isEqualTo(fileList)
    }

}
