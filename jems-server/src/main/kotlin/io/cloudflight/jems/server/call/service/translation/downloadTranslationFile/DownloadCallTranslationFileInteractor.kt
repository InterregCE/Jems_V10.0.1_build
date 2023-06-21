package io.cloudflight.jems.server.call.service.translation.downloadTranslationFile

interface DownloadCallTranslationFileInteractor {

    fun download(callId: Long, fileId: Long): Pair<String, ByteArray>

}
