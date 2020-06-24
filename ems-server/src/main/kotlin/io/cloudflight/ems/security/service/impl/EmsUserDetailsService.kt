package io.cloudflight.ems.security.service.impl;

import io.cloudflight.ems.service.AccountService
import io.cloudflight.ems.service.toLocalCurrentUser
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmsUserDetailsService(private val accountService: AccountService) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = accountService.findOneByEmail(email)
            ?: throw BadCredentialsException("Bad credentials")

        return user.toLocalCurrentUser();
    }
}


