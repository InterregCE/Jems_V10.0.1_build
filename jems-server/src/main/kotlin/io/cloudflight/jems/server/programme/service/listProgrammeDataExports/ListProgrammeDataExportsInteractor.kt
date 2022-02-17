package io.cloudflight.jems.server.programme.service.listProgrammeDataExports

import io.cloudflight.jems.server.programme.service.model.ProgrammeDataExportMetadata

interface ListProgrammeDataExportsInteractor {
    fun list(): List<ProgrammeDataExportMetadata>
}
