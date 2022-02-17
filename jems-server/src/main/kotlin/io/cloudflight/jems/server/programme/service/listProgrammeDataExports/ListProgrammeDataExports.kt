package io.cloudflight.jems.server.programme.service.listProgrammeDataExports

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanExportProgrammeData
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListProgrammeDataExports(val persistence: ProgrammeDataPersistence) :
    ListProgrammeDataExportsInteractor {

    @CanExportProgrammeData
    @Transactional
    @ExceptionWrapper(ListProgrammeDataExportException::class)
    override fun list() =
        persistence.listExportMetadata()
}
