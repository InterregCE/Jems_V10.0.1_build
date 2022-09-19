package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadContractingFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.fileManagement.ProjectContractingFilePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DownloadContractingFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 470L
    }

    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence

    @InjectMockKs
    lateinit var interactor: DownloadContractingFile

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
    }

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()
        every { contractingFilePersistence.downloadFile(PROJECT_ID, fileId = 14L) } returns file
        assertThat(interactor.download(PROJECT_ID, 14L)).isEqualTo(file)
    }

    @Test
    fun `download - not found`() {
        every { contractingFilePersistence.downloadFile(PROJECT_ID, fileId = -1L) } returns null
        assertThrows<FileNotFound> { interactor.download(PROJECT_ID, -1L) }
    }

}
