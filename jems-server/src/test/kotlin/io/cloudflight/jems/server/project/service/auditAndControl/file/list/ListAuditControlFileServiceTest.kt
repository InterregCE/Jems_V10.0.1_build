package io.cloudflight.jems.server.project.service.auditAndControl.file.list

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class ListAuditControlFileServiceTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 11L
        private const val AUDIT_CONTROL_ID = 13L
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistence

    @InjectMockKs
    lateinit var service: ListAuditControlFileService

    @Test
    fun list() {
        val expectedPath = "Project/000011/Report/Corrections/AuditControl/000013/"
        val fileList = mockk<Page<JemsFile>>()

        every { auditControlPersistence.getProjectIdForAuditControl(AUDIT_CONTROL_ID) } returns PROJECT_ID
        every { filePersistence.listAttachments(Pageable.unpaged(), expectedPath, setOf(JemsFileType.AuditControl), setOf()) } returns fileList


        Assertions.assertThat(service.list(AUDIT_CONTROL_ID, Pageable.unpaged())).isEqualTo(fileList)
    }

}
