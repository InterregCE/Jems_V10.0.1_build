package io.cloudflight.jems.server.project.service.report.project.annexes.delete

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DeleteProjectReportAnnexesFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 1L
        const val REPORT_ID = 2L
        const val FILE_ID = 3L
        const val pathPrefix = "Project/000001/Report/ProjectReport/000002/"
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    lateinit var deleteProjectReportAnnexesFile: DeleteProjectReportAnnexesFile

    @Test
    fun `should delete file`() {
        every { filePersistence.existsReportFile(PROJECT_ID, pathPrefix, FILE_ID) } returns true
        every { filePersistence.deleteReportFile(PROJECT_ID, FILE_ID) } returns Unit

        deleteProjectReportAnnexesFile.delete(PROJECT_ID, REPORT_ID, FILE_ID)

        verify { filePersistence.deleteReportFile(PROJECT_ID, FILE_ID) }
    }

    @Test
    fun `should throw FileNotFound when file does not exist`() {
        every { filePersistence.existsReportFile(PROJECT_ID, pathPrefix, FILE_ID) } returns false
        assertThrows<FileNotFound> { deleteProjectReportAnnexesFile.delete(PROJECT_ID, REPORT_ID, FILE_ID) }
    }
}
