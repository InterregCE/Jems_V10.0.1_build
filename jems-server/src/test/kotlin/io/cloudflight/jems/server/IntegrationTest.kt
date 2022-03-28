package io.cloudflight.jems.server

import io.cloudflight.jems.server.authentication.service.EmsUserDetailsService
import io.cloudflight.jems.server.call.controller.CustomFeignClientConfiguration
import io.cloudflight.platform.context.ApplicationContextProfiles
import org.junit.jupiter.api.ClassOrderer
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestClassOrder
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(ApplicationContextProfiles.TEST_CONTAINER)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestClassOrder(ClassOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class IntegrationTest {

    @Autowired
    private lateinit var authManager: AuthenticationManager

    @Autowired
    private lateinit var userDetailsService: EmsUserDetailsService

    companion object {
        val config = AnnotationConfigApplicationContext(
            FeignAutoConfiguration::class.java,
            CustomFeignClientConfiguration::class.java,
            HttpMessageConvertersAutoConfiguration::class.java
        )
    }

    fun loginWithUser(email: String, password: String) {
        SecurityContextHolder.getContext().authentication =
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    userDetailsService.loadUserByUsername(email), password
                )
            )
    }

    fun loginAsAdmin() {
        loginWithUser("admin@jems.eu", "Jems@2020admin@jems.eu")
    }
}
