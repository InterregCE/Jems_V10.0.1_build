package io.cloudflight.jems.server.user.service.user.get_user

import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetUserInteractor {

    fun getUsers(pageable: Pageable): Page<UserSummary>

    fun getUserById(userId: Long): User

}
