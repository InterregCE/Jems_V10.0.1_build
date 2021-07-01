package io.cloudflight.jems.server.programme.service.translation.list_translation_files

import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData

interface ListTranslationFilesInteractor {

    fun list(): List<TranslationFileMetaData>

}
