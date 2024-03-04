package io.cloudflight.jems.server.authentication.config

import io.cloudflight.platform.spring.context.ApplicationContextProfiles
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.HstsHeaderWriter
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy
import org.springframework.security.web.header.writers.XContentTypeOptionsHeaderWriter
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.Collections
import javax.servlet.http.HttpServletResponse


@AutoConfiguration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class SecurityConfig(
    private val environment: Environment,
) {

    companion object {
        private val WHITELIST = arrayOf(
            "/api/auth/**",
            "/api/_info/**",
            "/api/programmeLanguage/available/**",
            "/api/resources/logo/**",
            "/api/i18n/**",
            "/api/registration",
            "/api/captcha"
        )
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        if (!environment.acceptsProfiles(Profiles.of(ApplicationContextProfiles.TEST_CONTAINER))) {
            http.csrf().disable()
                // discuss enabling this to prevent CSRF attack
                //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .cors()
        } else {
            http.csrf().ignoringAntMatchers("/api/**")
        }

        http
            .authorizeRequests()
            .antMatchers(*WHITELIST).permitAll()
            .antMatchers("/api/**").fullyAuthenticated()
            .and()
            .httpBasic()
            // this exception handling automatically dismiss default browser "Sign in" pop-up for Basic auth
            .and()
            .exceptionHandling().authenticationEntryPoint { _, httpServletResponse, authException ->
                run {
                    httpServletResponse.setHeader("WWW-Authenticate", "FormBased")
                    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.message)
                }
            }
            .and()
            .logout()
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")

        http.headers()
            .addHeaderWriter(XContentTypeOptionsHeaderWriter())
            .addHeaderWriter(XFrameOptionsHeaderWriter())
            .addHeaderWriter(ReferrerPolicyHeaderWriter(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            .addHeaderWriter(XXssProtectionHeaderWriter())
            .addHeaderWriter(HstsHeaderWriter())

        return http.build()
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Throws(Exception::class)
    fun authenticationManager(authentication: AuthenticationConfiguration): AuthenticationManager {
        return authentication.getAuthenticationManager()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer =
        WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
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

    /**
     * needed for security context propagation for @async methods
     */
    @Bean
    fun taskExecutor(taskExecutor: AsyncTaskExecutor): DelegatingSecurityContextAsyncTaskExecutor {
        return DelegatingSecurityContextAsyncTaskExecutor(taskExecutor)
    }

}
