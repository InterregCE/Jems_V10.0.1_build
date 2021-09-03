package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.user.entity.UserEntity
import org.elasticsearch.common.inject.Inject
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import au.com.console.jpaspecificationdsl.*
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSearchRequest

@Service
class UserSpecification @Inject constructor(
    val userEntityRepository: UserEntityRepository,
    val userRolePersistenceProvider: UserRolePersistenceProvider) {

    fun hasName(name: String?): Specification<UserEntity>? = name?.let {
        UserEntity::name.like("%$name%")
    }

    fun hasSurname(surname: String?): Specification<UserEntity>? = surname?.let {
        UserEntity::surname.like("%$surname%")
    }

    fun hasEmail(email: String?): Specification<UserEntity>? = email?.let {
        UserEntity::email.like("%$email%")
    }

    fun hasRole(role: UserRoleSummary?): Specification<UserEntity>? = role?.let {
        UserEntity::userRole.equal(userRolePersistenceProvider.findUserRoleByName(role.name))
    }

    fun hasRoleIn(roles: List<UserRoleSummary>?): Specification<UserEntity>? = roles?.let {
        or(roles.map(::hasRole))
    }

    fun filter(userSearchRequest: UserSearchRequest): List<UserEntity> =
        userEntityRepository.findAll(
            and(
                hasName(userSearchRequest.userName),
                hasSurname(userSearchRequest.userSurname),
                hasEmail(userSearchRequest.userEmail),
                hasRoleIn(userSearchRequest.userRoles)
            )
        )
}

