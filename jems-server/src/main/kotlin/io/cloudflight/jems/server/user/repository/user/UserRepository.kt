package io.cloudflight.jems.server.user.repository.user

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Predicate
import io.cloudflight.jems.server.user.entity.QUserEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.service.model.UserSearchRequest
import io.cloudflight.jems.server.user.service.model.UserStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long>, QuerydslPredicateExecutor<UserEntity> {

    companion object {
        private val user = QUserEntity.userEntity

        private fun likeName(name: String?) = if (name.isNullOrBlank()) null else user.name.likeIgnoreCase("%${name}%")
        private fun likeSurname(surname: String?) =
            if (surname.isNullOrBlank()) null else user.surname.likeIgnoreCase("%${surname}%")

        private fun likeEmail(email: String?) = if (email.isNullOrBlank()) null else user.email.likeIgnoreCase("%${email}%")
        private fun hasAnyRole(roles: Set<Long>?) = if (roles.isNullOrEmpty()) null else user.userRole.id.`in`(roles)
        private fun hasAnyStatus(statuses: Set<UserStatus>?) =
            if (statuses.isNullOrEmpty()) null else user.userStatus.`in`(statuses)

        fun buildSearchPredicate(searchRequest: UserSearchRequest?): Predicate? =
            ExpressionUtils.allOf(
                likeName(searchRequest?.name),
                likeSurname(searchRequest?.surname),
                likeEmail(searchRequest?.email),
                hasAnyRole(searchRequest?.roles),
                hasAnyStatus(searchRequest?.userStatuses),
            )
    }

    fun getOneByEmail(email: String): UserEntity?

    fun existsByEmail(email: String): Boolean

    fun findAllByUserRoleIdInOrderByEmail(userRoleIds: Set<Long>): Iterable<UserEntity>

}
