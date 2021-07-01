package io.cloudflight.jems.server.programme.service.translation.list_translation_files

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.translation.TranslationFilePersistence
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListTranslationFiles(
    private val translationFilePersistence: TranslationFilePersistence,
) : ListTranslationFilesInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    @ExceptionWrapper(ListTranslationFilesException::class)
    override fun list(): List<TranslationFileMetaData> =
        translationFilePersistence.listTranslationFiles()

}
