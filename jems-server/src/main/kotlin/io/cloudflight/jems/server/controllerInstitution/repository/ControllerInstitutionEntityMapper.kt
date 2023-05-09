package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserId
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import io.cloudflight.jems.server.nuts.service.groupNuts
import io.cloudflight.jems.server.nuts.service.toOutputNuts
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import java.time.ZonedDateTime
import org.springframework.data.domain.Page

fun Page<ControllerInstitution>.toEntity() = map { it.toEntity() }
fun Page<ControllerInstitutionEntity>.toModel(institutionUsers: List<ControllerInstitutionUserEntity>) =
    map { institution -> institution.toModel(institutionUsers.filter { it.id.controllerInstitutionId == institution.id }) }

fun Page<ControllerInstitutionEntity>.toListModel() = map { it.toListModel() }

fun ControllerInstitutionEntity.toModel(institutionUsers: List<ControllerInstitutionUserEntity> = emptyList()) = ControllerInstitution (
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
    institutionNuts = institutionNuts.toDto(),
    institutionUsers = institutionUsers.map { institutionUserEntityToModel(it) }.toMutableSet()
)

fun ControllerInstitutionEntity.toListModel() = ControllerInstitutionList (
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
    institutionNuts = institutionNuts.toDto(),
)

fun ControllerInstitution.toEntity() =
    ControllerInstitutionEntity(
        id = id,
        name = name,
        description = description,
        createdAt = ZonedDateTime.now()
    )

fun UpdateControllerInstitution.toEntity() = ControllerInstitutionEntity(
        id = id,
        name = name,
        description = description,
        createdAt = ZonedDateTime.now(),
    )

fun List<ControllerInstitutionUser>.toEntity(
    institutionId: Long,
    users: List<UserEntity>
) = map { it.toEntity(institutionId, users.find { user -> user.email == it.userEmail }!!) }

fun ControllerInstitutionUser.toEntity(
    institutionId: Long,
    userEntity: UserEntity
): ControllerInstitutionUserEntity = ControllerInstitutionUserEntity(
    id = ControllerInstitutionUserId(
        controllerInstitutionId = institutionId,
        user = userEntity
    ),
    accessLevel = this.accessLevel,
)

fun ControllerInstitutionUserEntity.toModel() = ControllerInstitutionUser(
    institutionId = id.controllerInstitutionId,
    userId = id.user.id,
    userEmail = id.user.email,
    accessLevel = accessLevel
)

fun institutionUserEntityToModel(
    entity: ControllerInstitutionUserEntity,
): ControllerInstitutionUser = ControllerInstitutionUser(
    institutionId = entity.id.controllerInstitutionId,
    userId = entity.id.user.id,
    userEmail = entity.id.user.email,
    accessLevel = entity.accessLevel
)
fun Set<NutsRegion3>.toDto() = groupNuts(this).toOutputNuts()


fun ControllerInstitutionPartnerEntity.toFullModel() = InstitutionPartnerDetails(
    institutionId = institution?.id,
    partnerId = partnerId,
    partnerName = partnerAbbreviation,
    partnerStatus = partnerActive,
    partnerSortNumber = partnerNumber,
    partnerRole = partnerRole,
    partnerNuts3 = addressNuts3,
    partnerNuts3Code = addressNuts3Code,
    country = addressCountry,
    countryCode = addressCountryCode,
    city = addressCity,
    postalCode = addressPostalCode,
    callId = partner.project.call.id,
    projectId = partner.project.id,
    projectCustomIdentifier = projectIdentifier,
    projectAcronym = projectAcronym,
)

fun Page<ControllerInstitutionPartnerEntity>.toModel() = map { it.toFullModel() }
