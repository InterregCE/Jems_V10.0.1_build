package io.cloudflight.jems.server.authentication.service

import io.cloudflight.jems.server.user.entity.FailedLoginAttemptEntity
import io.cloudflight.jems.server.user.entity.FailedLoginAttemptId
import io.cloudflight.jems.server.user.repository.user.FailedLoginAttemptRepository
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class LoginAttemptServiceImpl(
    private val repository: FailedLoginAttemptRepository,
    private val userRepository: UserRepository
) : LoginAttemptService {


    @Transactional(readOnly = true)
    override fun getFailedLoginAttempt(email: String) =
        repository.findByEmail(email)

    @Transactional
    override fun saveFailedLoginAttempt(email: String, count: Short, lastAttemptAt: Instant) {
        userRepository.getOneByEmail(email)?.let { userEntity ->
            repository.save(
                FailedLoginAttemptEntity(
                    FailedLoginAttemptId(userEntity), count, lastAttemptAt
                )
            )
        }
    }

    @Transactional
    override fun deleteFailedLoginAttempt(email: String) {
        repository.deleteByEmail(email)
    }
}
