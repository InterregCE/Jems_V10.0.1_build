package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.server.programme.entity.ProgrammeDataEntity
import io.cloudflight.jems.server.programme.service.model.ProgrammeData
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

fun ProgrammeDataEntity.toModel() =
    mapper.map(this)

private val mapper = Mappers.getMapper(ProgrammeEntityModelMapper::class.java)

@Mapper
abstract class ProgrammeEntityModelMapper {
    abstract fun map(entity: ProgrammeDataEntity): ProgrammeData
}
