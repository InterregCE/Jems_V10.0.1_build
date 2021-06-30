package io.cloudflight.jems.server.common

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.server.call.service.model.IdNamePair
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val commonTOMapper = Mappers.getMapper(CommonDTOMapper::class.java)

fun List<IdNamePair>.toDTO() =
    map { it.toIdNamePairDTO() }

fun IdNamePair.toIdNamePairDTO() =
    commonTOMapper.map(this)

@Mapper
abstract class CommonDTOMapper {

    abstract fun map(idNamePair: IdNamePair): IdNamePairDTO
}



