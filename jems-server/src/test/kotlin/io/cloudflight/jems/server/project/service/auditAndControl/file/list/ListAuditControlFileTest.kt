package io.cloudflight.jems.server.project.service.auditAndControl.file.list

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ListAuditControlFileTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 11L
        private const val AUDIT_CONTROL_ID = 13L
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @InjectMockKs
    lateinit var interactor: ListAuditControlFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence, auditControlPersistence)
    }

    @Test
    fun list() {
        val path = JemsFileType.AuditControl.generatePath(PROJECT_ID, AUDIT_CONTROL_ID)
        val fileList = mockk<Page<JemsFile>>()

        every { auditControlPersistence.existsByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns true
        every { filePersistence.listAttachments(Pageable.unpaged(), path, setOf(JemsFileType.AuditControl), setOf()) } returns fileList

        assertThat(interactor.list(PROJECT_ID, AUDIT_CONTROL_ID, Pageable.unpaged())).isEqualTo(fileList)
    }

    @Test
    fun notFound() {
        every { auditControlPersistence.existsByIdAndProjectId(AUDIT_CONTROL_ID, PROJECT_ID) } returns false

        assertThrows<FileNotFound> {
            interactor.list(PROJECT_ID, AUDIT_CONTROL_ID, Pageable.unpaged())
        }
    }
}
