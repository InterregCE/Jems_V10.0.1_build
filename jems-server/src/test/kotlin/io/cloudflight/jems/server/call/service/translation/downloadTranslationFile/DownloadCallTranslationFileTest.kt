package io.cloudflight.jems.server.call.service.translation.downloadTranslationFile

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.translation.uploadTranslationFile.UploadCallTranslationFileTest.Companion.translationCallSpecific
import io.cloudflight.jems.server.call.service.translation.uploadTranslationFile.UploadCallTranslationFileTest.Companion.translationProcessed
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DownloadCallTranslationFileTest : UnitTest() {

    @MockK private lateinit var filePersistence: JemsFilePersistence

    @InjectMockKs private lateinit var interactor: DownloadCallTranslationFile

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence)
    }

    @Test
    fun download() {
        every { filePersistence.existsFile(exactPath = "CallTranslation/000007/", fileId = 87L) } returns true
        every { filePersistence.downloadFileAsStream(JemsFileType.CallTranslation, 87L) } returns
                Pair(translationCallSpecific, javaClass.classLoader.getResourceAsStream("call-specific-translation/$translationCallSpecific")!!)

        val fileWithName = interactor.download(7L, 87)

        assertThat(fileWithName.first).isEqualTo(translationCallSpecific)
        assertThat(fileWithName.second).isEqualTo(
            javaClass.classLoader.getResourceAsStream("call-specific-translation/$translationProcessed")!!.readAllBytes()
                .filter { it != '\r'.code.toByte() }.toByteArray()
        )
    }

    @Test
    fun `download - not existing`() {
        every { filePersistence.existsFile(exactPath = "CallTranslation/000018/", fileId = 22L) } returns false
        assertThrows<FileNotFound> { interactor.download(18L, 22L) }
        verify(exactly = 0) { filePersistence.downloadFileAsStream(any(), any()) }
    }

}
