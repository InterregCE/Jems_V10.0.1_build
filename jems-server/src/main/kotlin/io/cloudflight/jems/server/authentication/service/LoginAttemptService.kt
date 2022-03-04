package io.cloudflight.jems.server.authentication.service

import io.cloudflight.jems.server.user.entity.FailedLoginAttemptEntity
import java.time.Instant

interface LoginAttemptService {

    fun getFailedLoginAttempt(email: String): FailedLoginAttemptEntity?

    fun saveFailedLoginAttempt(email: String, count: Short, lastAttemptAt: Instant)

    fun deleteFailedLoginAttempt(email: String)
}
