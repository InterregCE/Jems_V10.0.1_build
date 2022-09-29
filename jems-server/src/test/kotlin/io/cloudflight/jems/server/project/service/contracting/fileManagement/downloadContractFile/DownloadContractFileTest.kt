package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadContractFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.fileManagement.FileNotFound
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DownloadContractFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 470L
    }

    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @InjectMockKs
    lateinit var interactor: DownloadContractFile

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
    }

    @Test
    fun `download contract file`() {
        val file = mockk<Pair<String, ByteArray>>()
        every { reportFilePersistence.getFileType(14L, PROJECT_ID) } returns ProjectPartnerReportFileType.Contract
        every { contractingFilePersistence.downloadFile(PROJECT_ID, fileId = 14L) } returns file
        assertThat(interactor.download(PROJECT_ID, 14L)).isEqualTo(file)
    }

    @Test
    fun `download - not found`() {
        every { reportFilePersistence.getFileType(-1, PROJECT_ID) } returns null
        every { contractingFilePersistence.downloadFile(PROJECT_ID, fileId = -1L) } returns null
        assertThrows<FileNotFound> { interactor.download(PROJECT_ID, -1L) }
    }

}
