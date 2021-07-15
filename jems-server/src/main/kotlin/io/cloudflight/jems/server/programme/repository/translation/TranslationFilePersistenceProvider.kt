package io.cloudflight.jems.server.programme.repository.translation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.programme.entity.translation.TranslationFileEntity
import io.cloudflight.jems.server.programme.entity.translation.TranslationFileId
import io.cloudflight.jems.server.programme.service.translation.TranslationFilePersistence
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import java.sql.Timestamp
import java.time.ZonedDateTime

private const val TRANSLATION_FILE_BUCKET_NAME = "jems-translation-file-bucket"
private const val TRANSLATION_FILE_ARCHIVE_BUCKET_NAME = "jems-translation-file-bucket-archive"

@Repository
class TranslationFilePersistenceProvider(
    private val resourceLoader: ResourceLoader,
    private val minioStorage: MinioStorage,
    private val translationFileRepository: TranslationFileRepository
) : TranslationFilePersistence {

    override fun exists(fileType: TranslationFileType, language: SystemLanguage): Boolean =
        minioStorage.exists(TRANSLATION_FILE_BUCKET_NAME, fileType.getFileNameFor(language))

    @Transactional
    override fun save(fileType: TranslationFileType, language: SystemLanguage, inputStream: InputStream, size: Long) =
        minioStorage.saveFile(TRANSLATION_FILE_BUCKET_NAME, fileType.getFileNameFor(language), size , inputStream).run {
            translationFileRepository.save(
                TranslationFileEntity(TranslationFileId(language, fileType), ZonedDateTime.now())
            ).toModel()
        }

    override fun archiveTranslationFile(fileType: TranslationFileType, language: SystemLanguage) {
        minioStorage.moveFile(TRANSLATION_FILE_BUCKET_NAME, fileType.getFileNameFor(language), TRANSLATION_FILE_ARCHIVE_BUCKET_NAME, fileType.getFileNameFor(language).plus("_").plus(Timestamp.from(ZonedDateTime.now().toInstant())))
    }

    override fun getTranslationFile(fileType: TranslationFileType, language: SystemLanguage): ByteArray =
        minioStorage.getFile(TRANSLATION_FILE_BUCKET_NAME, fileType.getFileNameFor(language))

    override fun getDefaultEnTranslationFile(fileType: TranslationFileType): ByteArray =
        resourceLoader.getResource("classpath:${fileType}_en.properties").inputStream.readAllBytes()

    @Transactional(readOnly = true)
    override fun listTranslationFiles(): List<TranslationFileMetaData> =
        translationFileRepository.findAll().toModel()

}
