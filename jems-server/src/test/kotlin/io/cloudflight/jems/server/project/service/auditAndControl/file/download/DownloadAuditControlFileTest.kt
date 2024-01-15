package io.cloudflight.jems.server.project.service.auditAndControl.file.download

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadAuditControlFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 21L
        const val AUDIT_CONTROL_ID = 22L
        const val FILE_ID = 23L
        fun filePath() = JemsFileType.AuditControl.generatePath(PROJECT_ID, AUDIT_CONTROL_ID)
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: DownloadAuditControlFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence)
    }

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()

        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns true
        every { filePersistence.downloadFile(type = JemsFileType.AuditControl, fileId = FILE_ID) } returns file

        assertThat(interactor.download(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID)).isEqualTo(file)
    }

    @Test
    fun `download - FileNotFound`() {
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns false

        assertThrows<FileNotFound> { interactor.download(PROJECT_ID, AUDIT_CONTROL_ID, FILE_ID) }
    }

}

