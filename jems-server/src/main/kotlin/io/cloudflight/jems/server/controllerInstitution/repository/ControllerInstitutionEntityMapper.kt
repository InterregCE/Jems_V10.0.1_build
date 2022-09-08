package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserId
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetailsRow
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignmentRow
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import io.cloudflight.jems.server.nuts.service.groupNuts
import io.cloudflight.jems.server.nuts.service.toOutputNuts
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import org.springframework.data.domain.Page
import java.time.ZonedDateTime

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


fun InstitutionPartnerDetailsRow.toModel() = InstitutionPartnerDetails(
    institutionId = institutionId,
    partnerId = partnerId,
    partnerName = partnerName,
    partnerStatus = partnerStatus,
    partnerSortNumber = partnerSortNumber,
    partnerRole = ProjectPartnerRole.valueOf(partnerRole),
    partnerNuts3 = partnerNuts3,
    partnerNuts3Code = partnerNuts3Code,
    country = country,
    countryCode = countryCode,
    city = city,
    postalCode = postalCode,
    callId = callId,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym
)

fun Page<InstitutionPartnerDetailsRow>.toModel() = map { it.toModel() }

fun List<InstitutionPartnerAssignmentRow>.toModels() = map { it.toModel() }

fun InstitutionPartnerAssignmentRow.toModel() = InstitutionPartnerAssignment(
    institutionId = institutionId,
    partnerId = partnerId,
    partnerProjectId = partnerProjectId
)

