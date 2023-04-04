package io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadInternalFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.contracting.fileManagement.FileNotFound
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

internal class DownloadInternalFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 470L
    }

    @MockK
    lateinit var contractingFilePersistence: ProjectContractingFilePersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence


    @InjectMockKs
    lateinit var interactor: DownloadInternalFile

    @BeforeEach
    fun setup() {
        clearMocks(contractingFilePersistence)
    }

    @Test
    fun `download internal file`() {
        val file = mockk<Pair<String, ByteArray>>()
        every { filePersistence.getFileType(14L, PROJECT_ID) } returns JemsFileType.ContractInternal
        every { contractingFilePersistence.downloadFile(PROJECT_ID, fileId = 14L) } returns file
        assertThat(interactor.download(PROJECT_ID, 14L)).isEqualTo(file)
    }
    @Test
    fun `download - not found`() {
        every { filePersistence.getFileType(-1, PROJECT_ID) } returns null
        every { contractingFilePersistence.downloadFile(PROJECT_ID, fileId = -1L) } returns null
        assertThrows<FileNotFound> { interactor.download(PROJECT_ID, -1L) }
    }

}
