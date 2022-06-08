package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.server.programme.entity.ProgrammeDataEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeDataExportMetadataEntity
import io.cloudflight.jems.server.programme.service.model.ProgrammeData
import io.cloudflight.jems.server.programme.service.model.ProgrammeDataExportMetadata
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProgrammeEntityModelMapper::class.java)

fun ProgrammeDataEntity.toModel() =
    mapper.map(this)

fun ProgrammeDataExportMetadataEntity.toModel() =
    mapper.map(this)

fun Iterable<ProgrammeDataExportMetadataEntity>.toModel() =
    this.map { mapper.map(it) }


@Mapper
abstract class ProgrammeEntityModelMapper {
    abstract fun map(entity: ProgrammeDataEntity): ProgrammeData
    abstract fun map(entity: ProgrammeDataExportMetadataEntity): ProgrammeDataExportMetadata
}
