package io.cloudflight.jems.server.authentication.repository

import io.cloudflight.jems.server.authentication.entity.PasswordResetTokenEntity
import io.cloudflight.jems.server.authentication.entity.PasswordResetTokenId
import io.cloudflight.jems.server.authentication.model.PasswordResetToken
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(SecurityEntityMapper::class.java)

fun PasswordResetToken.toEntity(passwordResetTokenId: PasswordResetTokenId) =
    mapper.map(this, passwordResetTokenId)

fun PasswordResetTokenEntity.toModel(user: UserSummary) =
    mapper.map(this, user)

@Mapper
interface SecurityEntityMapper {
    @Mappings(
        Mapping(target = "id", source = "passwordResetTokenId")
    )
    fun map(model: PasswordResetToken, passwordResetTokenId: PasswordResetTokenId): PasswordResetTokenEntity
    fun map(entity: PasswordResetTokenEntity, user: UserSummary): PasswordResetToken
}
