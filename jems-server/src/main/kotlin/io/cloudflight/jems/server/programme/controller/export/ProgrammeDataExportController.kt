package io.cloudflight.jems.server.programme.controller.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.export.ProgrammeDataExportApi
import io.cloudflight.jems.server.common.toResponseEntity
import io.cloudflight.jems.server.programme.service.export_programme_data.ExportProgrammeDataInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeDataExportController(private val exportProgrammeData: ExportProgrammeDataInteractor) : ProgrammeDataExportApi {

    override fun export(
        pluginKey: String?, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage
    ): ResponseEntity<ByteArrayResource> =
        exportProgrammeData.export(pluginKey, exportLanguage, inputLanguage).toResponseEntity()
}
