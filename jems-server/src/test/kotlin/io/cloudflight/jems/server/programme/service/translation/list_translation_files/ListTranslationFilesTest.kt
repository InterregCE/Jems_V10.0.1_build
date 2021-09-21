package io.cloudflight.jems.server.programme.service.translation.list_translation_files

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.translation.TranslationFilePersistence
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class ListTranslationFilesTest : UnitTest() {

    private val fileType = TranslationFileType.System
    private val language = SystemLanguage.EN
    private val fileMetaData = TranslationFileMetaData(language, fileType, ZonedDateTime.now())


    @MockK
    lateinit var translationFilePersistence: TranslationFilePersistence

    @InjectMockKs
    lateinit var listTranslationFiles: ListTranslationFiles

    @Test
    fun `should return list of translation files meta data`() {

        every {
            translationFilePersistence.listTranslationFiles()
        } returns listOf(fileMetaData)

        assertThat(listTranslationFiles.list()).containsExactly(fileMetaData)
    }
}
