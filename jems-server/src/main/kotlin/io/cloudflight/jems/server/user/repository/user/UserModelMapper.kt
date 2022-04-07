package io.cloudflight.jems.server.user.repository.user

import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.cloudflight.jems.server.user.service.model.UserWithPassword
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

private val mapper = Mappers.getMapper(UserModelMapper::class.java)

fun UserEntity.toModel(permissions: Set<UserRolePermission>) =
    mapper.mapToUser(this, userRole.toModel(permissions), emptySet())

fun UserEntity.toModelWithPassword(permissions: Set<UserRolePermission>) =
    mapper.mapToUserWithPassword(this, userRole.toModel(permissions), emptySet())

fun UserRoleEntity.toModel(permissions: Set<UserRolePermission>) =
    mapper.map(this, permissions)

fun Page<UserEntity>.toModel() =
    map { mapper.map(it) }

fun UserChange.toEntity(passwordEncoded: String, role: UserRoleEntity) =
    mapper.map(this, passwordEncoded, role)

fun UserEntity.toUserSummary() =
    mapper.map(this)

fun UserWithPassword.toUserSummary() =
    mapper.map(this)

@Mapper
interface UserModelMapper {
    @Mapping(target = "isDefault", constant = "false")
    fun map(userRole: UserRole): UserRoleSummary
    @Mapping(target = "isDefault", constant = "false")
    fun mapToSummary(userRoleEntity: UserRoleEntity): UserRoleSummary
    fun map(userWithPassword: UserWithPassword): UserSummary
    fun map(userEntity: UserEntity): UserSummary
    @Mappings(
        Mapping(target = "id", source = "userEntity.id" ),
        Mapping(target = "name", source = "userEntity.name" ),
        Mapping(target = "encodedPassword", source = "userEntity.password" ),
        Mapping(target = "userRole", source = "userRole" ),
        Mapping(target = "assignedProjects", source = "assignedProjects" )
    )
    fun mapToUserWithPassword(userEntity: UserEntity, userRole: UserRole, assignedProjects: Set<Long>): UserWithPassword
    @Mappings(
        Mapping(target = "id", source = "userEntity.id" ),
        Mapping(target = "name", source = "userEntity.name" ),
        Mapping(target = "userRole", source = "userRole" ),
        Mapping(target = "assignedProjects", source = "assignedProjects" )
    )
    fun mapToUser(userEntity: UserEntity, userRole: UserRole, assignedProjects: Set<Long>): User
    @Mappings(
        Mapping(target = "id", source = "userChange.id" ),
        Mapping(target = "name", source = "userChange.name" )
    )
    fun map(userChange: UserChange, password: String, userRole: UserRoleEntity): UserEntity

    @Mapping(target = "isDefault", constant = "false")
    fun map(userRoleEntity: UserRoleEntity, permissions: Set<UserRolePermission>): UserRole
}
