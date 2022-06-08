package io.cloudflight.jems.server.programme.controller.export

import io.cloudflight.jems.api.programme.dto.export.ProgrammeDataExportMetadataDTO
import io.cloudflight.jems.server.programme.service.model.ProgrammeDataExportMetadata
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProgrammeDataExportDTOMapper::class.java)

fun List<ProgrammeDataExportMetadata>.toDTO() =
    map { model -> mapper.map(model) }

@Mapper
interface ProgrammeDataExportDTOMapper {
    fun map(programmeDataExportMetadata: ProgrammeDataExportMetadata): ProgrammeDataExportMetadataDTO
}
