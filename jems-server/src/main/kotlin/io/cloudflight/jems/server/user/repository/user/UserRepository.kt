package io.cloudflight.jems.server.user.repository.user

import com.querydsl.core.types.ExpressionUtils
import io.cloudflight.jems.server.user.entity.QUserEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.service.model.UserSearchRequest
import io.cloudflight.jems.server.user.service.model.UserStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

interface QueryDslUserRepository {
    fun findAll(pageable: Pageable, searchRequest: UserSearchRequest?): Page<UserEntity>
}

open class QueryDslUserRepositoryImpl(
    private val userRepository: UserRepository
) : QueryDslUserRepository {

    private val user = QUserEntity.userEntity

    private fun likeName(name: String?) = if (name.isNullOrBlank()) null else user.name.likeIgnoreCase("%${name}%")
    private fun likeSurname(surname: String?) =
        if (surname.isNullOrBlank()) null else user.surname.likeIgnoreCase("%${surname}%")

    private fun likeEmail(email: String?) = if (email.isNullOrBlank()) null else user.email.likeIgnoreCase("%${email}%")
    private fun hasAnyRole(roles: Set<Long>?) = if (roles.isNullOrEmpty()) null else user.userRole.id.`in`(roles)
    private fun hasAnyStatus(statuses: Set<UserStatus>?) =
        if (statuses.isNullOrEmpty()) null else user.userStatus.`in`(statuses)

    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable, searchRequest: UserSearchRequest?): Page<UserEntity> =
        ExpressionUtils.allOf(
            likeName(searchRequest?.name),
            likeSurname(searchRequest?.surname),
            likeEmail(searchRequest?.email),
            hasAnyRole(searchRequest?.roles),
            hasAnyStatus(searchRequest?.userStatuses),
        ).let { predicate ->
            if (predicate != null) userRepository.findAll(predicate, pageable)
            else userRepository.findAll(pageable)
        }
}

@Repository
interface UserRepository : JpaRepository<UserEntity, Long>, QueryDslUserRepository,
    QuerydslPredicateExecutor<UserEntity> {

    fun getOneByEmail(email: String): UserEntity?

    fun existsByEmail(email: String): Boolean

}
