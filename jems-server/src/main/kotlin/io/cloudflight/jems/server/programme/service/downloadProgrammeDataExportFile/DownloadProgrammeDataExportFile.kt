package io.cloudflight.jems.server.programme.service.downloadProgrammeDataExportFile

import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanExportProgrammeData
import io.cloudflight.jems.server.programme.service.model.ProgrammeDataExportMetadata
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadProgrammeDataExportFile(val persistence: ProgrammeDataPersistence) :
    DownloadProgrammeDataExportFileInteractor {

    @CanExportProgrammeData
    @Transactional
    @ExceptionWrapper(DownloadProgrammeDataExportFileException::class)
    override fun download(pluginKey: String?): ExportResult =
        if (pluginKey.isNullOrBlank())
            throw PluginKeyIsInvalidException()
        else
            persistence.getExportMetaData(pluginKey).let { metadata ->
                throwIfFileIsNotReady(metadata)
                ExportResult(
                    metadata.contentType!!, metadata.fileName!!, persistence.getExportFile(pluginKey),
                    startTime = metadata.exportStartedAt, endTime = metadata.exportEndedAt
                )
            }

    private fun throwIfFileIsNotReady(metadata: ProgrammeDataExportMetadata) {
        if (metadata.fileName.isNullOrBlank() || metadata.contentType.isNullOrBlank() || metadata.exportEndedAt == null)
            throw ExportFileIsNotReadyException()
    }
}
