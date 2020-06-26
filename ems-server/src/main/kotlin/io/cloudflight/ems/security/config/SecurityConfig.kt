package io.cloudflight.ems.security.config

import io.cloudflight.ems.security.service.impl.EmsUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.Collections


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfig(val emsUserDetailsService: EmsUserDetailsService, val passwordEncoder: PasswordEncoder) :
    WebSecurityConfigurerAdapter() {

    companion object {
        private val WHITELIST = arrayOf(
            "/api/auth/**"
        )
    }

    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable()
            .cors()
            .and()
            .authorizeRequests()
            .antMatchers(*WHITELIST).permitAll()
            .antMatchers("/api/**").fullyAuthenticated()
            .and()
            .httpBasic()
            .and()
            .logout()
            .invalidateHttpSession(true)
    }

    override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder
            .userDetailsService<UserDetailsService>(emsUserDetailsService)
            .passwordEncoder(passwordEncoder)
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }

    override fun configure(webSecurity: WebSecurity) {
        // permit all static resources
        webSecurity.ignoring()
            .antMatchers("/**/*.{js|css|html}")
            .antMatchers("/api/i18n/**")
            .antMatchers("/favicon.ico")
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val configuration = CorsConfiguration().applyPermitDefaultValues()
        configuration.allowedOrigins = Collections.singletonList("*")
        configuration.allowedMethods = listOf("GET", "POST", "DELETE", "OPTIONS", "PUT")

        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
