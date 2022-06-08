package io.cloudflight.jems.server.programme.controller.export

import io.cloudflight.jems.api.programme.dto.export.ProgrammeDataExportMetadataDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.export.ProgrammeDataExportApi
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.programme.service.downloadProgrammeDataExportFile.DownloadProgrammeDataExportFileInteractor
import io.cloudflight.jems.server.programme.service.exportProgrammeData.ExportProgrammeDataInteractor
import io.cloudflight.jems.server.programme.service.listProgrammeDataExports.ListProgrammeDataExportsInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeDataExportController(
    private val exportProgrammeData: ExportProgrammeDataInteractor,
    private val listProgrammeDataExports: ListProgrammeDataExportsInteractor,
    private val downloadProgrammeDataExportFile: DownloadProgrammeDataExportFileInteractor
) : ProgrammeDataExportApi {

    override fun export(pluginKey: String?, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage) =
        exportProgrammeData.export(pluginKey, exportLanguage, inputLanguage)

    override fun list(): List<ProgrammeDataExportMetadataDTO> =
        listProgrammeDataExports.list().toDTO()

    override fun download(pluginKey: String?): ResponseEntity<ByteArrayResource> =
        downloadProgrammeDataExportFile.download(pluginKey).toResponseEntity()
}
