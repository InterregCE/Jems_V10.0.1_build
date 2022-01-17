package io.cloudflight.jems.server.user.repository.confirmation

import io.cloudflight.jems.server.user.entity.UserConfirmationEntity
import io.cloudflight.jems.server.user.service.model.UserConfirmation
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(UserConfirmationMapper::class.java)

fun UserConfirmation.toEntity() =
    mapper.map(this)

fun UserConfirmationEntity.toModel() =
    mapper.map(this)

@Mapper
abstract class UserConfirmationMapper {
    abstract fun map(model: UserConfirmation): UserConfirmationEntity
    abstract fun map(entity: UserConfirmationEntity): UserConfirmation
}
