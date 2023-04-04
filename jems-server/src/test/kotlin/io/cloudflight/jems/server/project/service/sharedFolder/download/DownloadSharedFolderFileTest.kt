package io.cloudflight.jems.server.project.service.sharedFolder.download

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.sharedFolderFile.download.DownloadSharedFolderFile
import io.cloudflight.jems.server.project.service.sharedFolderFile.download.FileNotFound
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadSharedFolderFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 345L
        private const val FILE_ID = 678L
    }

    @MockK
    private lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs
    private lateinit var interactor: DownloadSharedFolderFile

    @BeforeEach
    fun setUp() {
        clearMocks(filePersistence)
    }

    @Test
    fun download() {
        val file = mockk<Pair<String, ByteArray>>()
        every { filePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(PROJECT_ID, FILE_ID, setOf(JemsFileType.SharedFolder)) } returns true
        every { filePersistence.downloadFile(JemsFileType.SharedFolder, FILE_ID) } returns file

        assertThat(interactor.download(PROJECT_ID, FILE_ID)).isEqualTo(file)
    }

    @Test
    fun downloadFileNotFound() {
        every { filePersistence.existsFileByProjectIdAndFileIdAndFileTypeIn(PROJECT_ID, FILE_ID, setOf(JemsFileType.SharedFolder)) } returns false

        assertThrows<FileNotFound> { interactor.download(PROJECT_ID, FILE_ID) }
    }

}
