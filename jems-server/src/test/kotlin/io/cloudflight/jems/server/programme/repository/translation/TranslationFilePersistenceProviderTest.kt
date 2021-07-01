package io.cloudflight.jems.server.programme.repository.translation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.programme.entity.translation.TranslationFileEntity
import io.cloudflight.jems.server.programme.entity.translation.TranslationFileId
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.ByteArrayInputStream
import java.time.ZonedDateTime

internal class TranslationFilePersistenceProviderTest : UnitTest() {

    private val fileSize = 50L
    private val fileType = TranslationFileType.System
    private val language = SystemLanguage.EN
    private val fileInputStream = ByteArrayInputStream(ByteArray(fileSize.toInt()))
    private val fileMetaData = TranslationFileMetaData(language, fileType, ZonedDateTime.now())
    private val fileMetaDataEntity = TranslationFileEntity(TranslationFileId(language, fileType), ZonedDateTime.now())

    @MockK
    lateinit var minioStorage: MinioStorage

    @MockK
    lateinit var translationFileRepository: TranslationFileRepository

    @InjectMockKs
    lateinit var translationFilePersistenceProvider: TranslationFilePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(translationFileRepository)
        clearMocks(minioStorage)
    }


    @TestFactory
    fun `should return correctly when existence of translation file is being checked`() =
        listOf(
            false, true
        ).map { input ->
            DynamicTest.dynamicTest(
                "should return ${input} when existence of translation file is being checked"
            ) {
                val slot = slot<String>()
                every { minioStorage.exists(capture(slot), fileType.getFileNameFor(language)) } returns input

                assertThat(translationFilePersistenceProvider.exists(fileType, language))
                    .isEqualTo(input)
                verify(exactly = 1) { minioStorage.exists(slot.captured, fileType.getFileNameFor(language)) }
                clearMocks(minioStorage)
            }
        }


    @Test
    fun `should upload translation file and save file meta data when file does not already exist`() {

        val slot = slot<TranslationFileEntity>()
        every {
            minioStorage.saveFile(any(), fileType.getFileNameFor(language), fileSize, fileInputStream
            )
        } returns Unit
        every {
            translationFileRepository.save(capture(slot))
        } returnsArgument 0

        assertThat(translationFilePersistenceProvider.save(fileType, language, fileInputStream, fileSize))
            .isEqualTo(fileMetaData.copy(lastModified = slot.captured.lastModified))

        verify(exactly = 1) {
            minioStorage.saveFile(
                any(), fileType.getFileNameFor(language), fileSize, fileInputStream
            )
        }
        verify(exactly = 1) { translationFileRepository.save(slot.captured) }

    }

    @Test
    fun `should archive existing translation file `() {

        every { minioStorage.moveFile(any(), any(), any(), any()) } returns Unit

        translationFilePersistenceProvider.archiveTranslationFile(fileType, language)

        verify(exactly = 1) { minioStorage.moveFile(any(), any(), any(), any()) }

    }

    @Test
    fun `should return ByteArray of the translation file `() {

        val fileByteArray = ByteArray(50)

        every { minioStorage.getFile(any(), fileType.getFileNameFor(language)) } returns fileByteArray

        assertThat(translationFilePersistenceProvider.getTranslationFile(fileType, language)).isEqualTo(fileByteArray)

        verify(exactly = 1) { minioStorage.getFile(any(), fileType.getFileNameFor(language)) }

    }

    @Test
    fun `should return list of translation files meta data `() {

        every { translationFileRepository.findAll() } returns listOf(fileMetaDataEntity)

        assertThat(translationFilePersistenceProvider.listTranslationFiles()).containsAll(listOf(fileMetaData.copy(lastModified = fileMetaDataEntity.lastModified)))

        verify(exactly = 1) { translationFileRepository.findAll() }

    }
}
