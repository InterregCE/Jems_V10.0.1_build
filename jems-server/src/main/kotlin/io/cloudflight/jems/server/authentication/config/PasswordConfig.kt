package io.cloudflight.jems.server.authentication.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class PasswordConfig {

    companion object {
        const val PASSWORD_ENCODER = "bcrypt"
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        val encoders = mapOf(PASSWORD_ENCODER to BCryptPasswordEncoder());
        val passworEncoder = DelegatingPasswordEncoder(PASSWORD_ENCODER, encoders)
        passworEncoder.setDefaultPasswordEncoderForMatches(encoders[PASSWORD_ENCODER]);
        return passworEncoder
    }
}
