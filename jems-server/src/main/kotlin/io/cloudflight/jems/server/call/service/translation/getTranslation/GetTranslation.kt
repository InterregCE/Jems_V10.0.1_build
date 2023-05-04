package io.cloudflight.jems.server.call.service.translation.getTranslation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.call.authorization.CanRetrieveCall
import io.cloudflight.jems.server.call.service.model.translation.CallTranslationFile
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.CallTranslation
import io.cloudflight.jems.server.programme.service.translation.TranslationFilePersistence
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetTranslation(
    private val filePersistence: JemsFilePersistence,
    private val translationFilePersistence: TranslationFilePersistence,
) : GetTranslationInteractor {

    companion object {
        private val GET_LANGUAGE_FROM_NAME_REGEX = Regex("call-id-\\d+-Application_(\\w{2})\\.properties")
    }

    @CanRetrieveCall
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetTranslationException::class)
    override fun get(callId: Long): List<CallTranslationFile> {
        val files = filePersistence.listAttachments(
            pageable = Pageable.ofSize(SystemLanguage.values().size),
            indexPrefix = CallTranslation.generatePath(callId),
            setOf(CallTranslation), emptySet(),
        ).associateBy { it.getLanguage() }

        val defaultProgrammeFiles = translationFilePersistence.listTranslationFiles()
            .filter { it.fileType == TranslationFileType.Application }
            .associateBy { it.language }.mapValues { "Application_${it.value.language.name.lowercase()}.properties" }

        return SystemLanguage.values().map { CallTranslationFile(it, files[it]?.toMeta(), defaultProgrammeFiles[it]) }
    }

    private fun JemsFile.toMeta() = JemsFileMetadata(id, name, uploaded)

    private fun JemsFile.getLanguage() = SystemLanguage.valueOf(
        GET_LANGUAGE_FROM_NAME_REGEX.matchEntire(name)!!.groups.get(1)!!.value.uppercase()
    )

}
