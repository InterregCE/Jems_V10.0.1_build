package io.cloudflight.jems.server.programme.service.translation.download_translation_file

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.translation.TranslationFilePersistence
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class DownloadTranslationFileTest : UnitTest() {

    @MockK
    lateinit var translationFilePersistence: TranslationFilePersistence

    @InjectMockKs
    lateinit var downloadTranslationFile: DownloadTranslationFile

    @Test
    fun `should return ByteArray of the translation file`() {
        val file = ByteArray(50)
        every {
            translationFilePersistence.getTranslationFile(TranslationFileType.System, SystemLanguage.EN)
        } returns file

        assertThat(downloadTranslationFile.download(TranslationFileType.System, SystemLanguage.EN))
            .isEqualTo(file)
    }
}
