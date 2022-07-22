package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserId
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import io.cloudflight.jems.server.nuts.service.groupNuts
import io.cloudflight.jems.server.nuts.service.toOutputNuts
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.user.controller.toEntity
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.data.domain.Page
import java.time.ZonedDateTime

fun Page<ControllerInstitution>.toEntity() = map { it.toEntity() }
fun Page<ControllerInstitutionEntity>.toModel(institutionUsers: List<ControllerInstitutionUserEntity>) =
    map { institution -> institution.toModel(institutionUsers.filter { it.id.controllerInstitutionId == institution.id }) }

fun Page<ControllerInstitutionEntity>.toListModel() = map { it.toListModel() }

fun ControllerInstitutionEntity.toModel(institutionUsers: List<ControllerInstitutionUserEntity>) = ControllerInstitution (
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
    institutionNuts = institutionNuts.toDto(),
    institutionUsers = institutionUsers.map { institutionUserEntityToModel(it) }
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
    controllerInstitution: ControllerInstitutionEntity,
    users: List<UserSummary>
) = map { it.toEntity(controllerInstitution, users.find { user -> user.email == it.userEmail }!!) }

fun ControllerInstitutionUser.toEntity(
    institutionEntity: ControllerInstitutionEntity,
    userSummary: UserSummary
): ControllerInstitutionUserEntity = ControllerInstitutionUserEntity(
    id = ControllerInstitutionUserId(
        controllerInstitutionId = institutionEntity.id,
        user = userSummary.toEntity()
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



