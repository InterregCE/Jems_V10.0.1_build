package io.cloudflight.jems.server.project.service.report.project.verification.file.delete

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class DeleteProjectReportVerificationFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 51L
        const val REPORT_ID = 55L
        const val FILE_ID = 59L
        fun filePath() = VerificationDocument.generatePath(PROJECT_ID, REPORT_ID)
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var interactor: DeleteProjectReportVerificationFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence)
    }

    @Test()
    fun delete() {
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns true
        every { filePersistence.deleteFile(VerificationDocument, FILE_ID) } returns Unit

        assertDoesNotThrow { interactor.delete(PROJECT_ID, REPORT_ID, FILE_ID) }
    }

    @Test()
    fun `delete - FileNotFound`() {
        every { filePersistence.existsFile(exactPath = filePath(), fileId = FILE_ID) } returns false

        assertThrows<FileNotFound> { interactor.delete(PROJECT_ID, REPORT_ID, FILE_ID) }
    }
}
