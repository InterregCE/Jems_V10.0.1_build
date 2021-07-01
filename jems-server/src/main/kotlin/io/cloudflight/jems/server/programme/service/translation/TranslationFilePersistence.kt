package io.cloudflight.jems.server.programme.service.translation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import java.io.InputStream

interface TranslationFilePersistence {

    fun exists(fileType: TranslationFileType, language: SystemLanguage): Boolean

    fun save(fileType: TranslationFileType, language: SystemLanguage, inputStream: InputStream, size: Long) : TranslationFileMetaData

    fun archiveTranslationFile(fileType: TranslationFileType, language: SystemLanguage)

    fun getTranslationFile(fileType: TranslationFileType, language: SystemLanguage): ByteArray

    fun listTranslationFiles(): List<TranslationFileMetaData>
}
