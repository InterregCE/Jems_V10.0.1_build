package io.cloudflight.ems.security.config

import io.cloudflight.ems.security.ROLE_ADMIN
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.dto.User
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*

/**
 * have your architect create ANOTHER security config for the 'production' login
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@Profile("!production")
class NoAuthWebSecurityConfig : WebSecurityConfigurerAdapter() {

    // WIll revert commented parts once login is necessary.
    override fun configure(http: HttpSecurity) {
        http
//            .cors()
//            .and()
//
//            .authorizeRequests()
//            .antMatchers("/api/**").fullyAuthenticated()
//            .and()
//
//            .csrf().disable()
//
//            .logout().permitAll()
//            .and()

            .httpBasic()
    }

    override fun configure(webSecurity: WebSecurity) {
        // permit all static resources
        webSecurity.ignoring()
            .antMatchers(HttpMethod.POST)
            .antMatchers("/**/*.{js|css|html}")
            .antMatchers("/api/i18n/**")
            .antMatchers("/favicon.ico")
    }

    @Configuration
    class NoAuthenticationConfiguration : GlobalAuthenticationConfigurerAdapter() {
        companion object {
            val registeredUsers = listOf(
                Pair(User(1, "admin", "User with admin role"), listOf(SimpleGrantedAuthority(ROLE_ADMIN))),
                Pair(User(2, "user", "User with user role"), emptyList())
            ).map { it.first.username to it }.toMap()
        }

        override fun init(auth: AuthenticationManagerBuilder) {
            auth.authenticationProvider(object : AbstractUserDetailsAuthenticationProvider() {
                override fun additionalAuthenticationChecks(userDetails: UserDetails, authentication: UsernamePasswordAuthenticationToken) {
                    // no authentication checks in DEV mode, so that we can login with any password
                }

                override fun retrieveUser(username: String, authentication: UsernamePasswordAuthenticationToken): UserDetails {
                    val userWithRoles = registeredUsers[username] ?: throw UsernameNotFoundException("username")

                    return LocalCurrentUser(
                        userWithRoles.first,
                        userWithRoles.second
                    )
                }
            })
        }
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
