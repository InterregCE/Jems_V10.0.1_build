package io.cloudflight.jems.server.utils

import io.cloudflight.jems.server.authentication.service.AuthenticationServiceImpl.Companion.MAX_ALLOWED_NUMBER_OF_LOGIN_ATTEMPTS
import io.cloudflight.jems.server.user.entity.FailedLoginAttemptEntity
import io.cloudflight.jems.server.user.entity.FailedLoginAttemptId
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import java.time.Instant


const val loginEmail = "some@applicant"
val userEntityOfLogin = UserEntity(
    1L, loginEmail, "name", "surname", UserRoleEntity(1L, "applicant"),
    "", UserStatus.ACTIVE
)

fun failedLoginAttemptEntity(
    count: Short = MAX_ALLOWED_NUMBER_OF_LOGIN_ATTEMPTS,
    lastAttemptAt: Instant = Instant.now()
) = FailedLoginAttemptEntity(
    FailedLoginAttemptId(userEntityOfLogin), count, lastAttemptAt
)
