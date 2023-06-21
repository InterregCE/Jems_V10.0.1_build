package io.cloudflight.jems.server.call.service.translation.downloadTranslationFile

import io.cloudflight.jems.server.call.authorization.CanRetrieveCall
import io.cloudflight.jems.server.call.service.translation.removeCallIdPrefixes
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.CallTranslation
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadCallTranslationFile(
    private val filePersistence: JemsFilePersistence,
) : DownloadCallTranslationFileInteractor {

    @CanRetrieveCall
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadCallTranslationFileException::class)
    override fun download(callId: Long, fileId: Long): Pair<String, ByteArray> {
        if (!filePersistence.existsFile(exactPath = CallTranslation.generatePath(callId), fileId = fileId))
            throw FileNotFound()

        return filePersistence.downloadFileAsStream(CallTranslation, fileId)!!.removeCallIdPrefixes(callId)
    }

}
