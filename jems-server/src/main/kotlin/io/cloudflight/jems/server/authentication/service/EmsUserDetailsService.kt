package io.cloudflight.jems.server.authentication.service;

import io.cloudflight.jems.server.user.service.UserService
import io.cloudflight.jems.server.user.service.toLocalCurrentUser
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmsUserDetailsService(private val userService: UserService) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userService.findOneByEmail(email)
            ?: throw BadCredentialsException("Bad credentials")

        return user.toLocalCurrentUser();
    }
}


