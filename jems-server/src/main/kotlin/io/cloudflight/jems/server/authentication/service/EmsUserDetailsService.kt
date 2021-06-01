package io.cloudflight.jems.server.authentication.service;

import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.toLocalCurrentUser
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmsUserDetailsService(
    private val userPersistence: UserPersistence,
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userPersistence.getByEmail(email)
            ?: throw BadCredentialsException("Bad credentials")

        return user.toLocalCurrentUser()
    }
}


