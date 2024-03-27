package io.cloudflight.jems.server.authentication.config

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@AutoConfiguration
class PasswordConfig {

    companion object {
        const val PASSWORD_ENCODER = "bcrypt"
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        val encoders = mapOf(PASSWORD_ENCODER to BCryptPasswordEncoder());
        val passwordEncoder = DelegatingPasswordEncoder(PASSWORD_ENCODER, encoders)
        passwordEncoder.setDefaultPasswordEncoderForMatches(encoders[PASSWORD_ENCODER]);
        return passwordEncoder
    }
}
