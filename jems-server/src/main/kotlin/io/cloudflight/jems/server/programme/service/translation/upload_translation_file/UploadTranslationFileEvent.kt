package io.cloudflight.jems.server.programme.service.translation.upload_translation_file

import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import org.springframework.context.ApplicationEvent
import java.io.InputStream

class UploadTranslationFileEvent(
    val context: Any,
    val translationFileMetaData: TranslationFileMetaData,
    val translationFile: InputStream
) : ApplicationEvent(context)
