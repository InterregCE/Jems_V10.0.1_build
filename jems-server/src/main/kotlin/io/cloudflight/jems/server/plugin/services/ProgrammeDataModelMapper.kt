package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.programme.ProgrammeInfoData
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.model.ProgrammeData
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

fun ProgrammeData.toDataModel(funds: List<ProgrammeFund>) =
    mapper.map(this, funds)

private val mapper = Mappers.getMapper(ProgrammeDataModelMapper::class.java)

@Mapper
abstract class ProgrammeDataModelMapper {
    @Mapping(target = "funds", source = "funds")
    abstract fun map(programmeData: ProgrammeData, funds: List<ProgrammeFund>): ProgrammeInfoData
}
