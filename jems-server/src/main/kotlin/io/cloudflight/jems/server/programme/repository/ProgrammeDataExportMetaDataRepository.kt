package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.server.programme.entity.ProgrammeDataExportMetadataEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProgrammeDataExportMetaDataRepository : CrudRepository<ProgrammeDataExportMetadataEntity, String>{
    fun findAllByOrderByRequestTimeDesc() : List<ProgrammeDataExportMetadataEntity>
}
