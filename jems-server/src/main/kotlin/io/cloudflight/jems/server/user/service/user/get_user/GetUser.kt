package io.cloudflight.jems.server.user.service.user.get_user

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.user.service.authorization.CanRetrieveUser
import io.cloudflight.jems.server.user.service.authorization.CanRetrieveUsers
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUser(private val persistence: UserPersistence) : GetUserInteractor {

    @CanRetrieveUsers
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetUserException::class)
    override fun getUsers(pageable: Pageable): Page<UserSummary> =
        persistence.findAll(pageable)

    @CanRetrieveUser
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetUserException::class)
    override fun getUserById(userId: Long): User =
        persistence.getById(userId).getUser()

}
