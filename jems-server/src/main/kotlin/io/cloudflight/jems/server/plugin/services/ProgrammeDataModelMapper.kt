package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.programme.ProgrammeInfoData
import io.cloudflight.jems.server.programme.service.model.ProgrammeData
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

fun ProgrammeData.toDataModel() =
    mapper.map(this)

private val mapper = Mappers.getMapper(ProgrammeDataModelMapper::class.java)

@Mapper
abstract class ProgrammeDataModelMapper {
    abstract fun map(programmeData: ProgrammeData): ProgrammeInfoData
}
