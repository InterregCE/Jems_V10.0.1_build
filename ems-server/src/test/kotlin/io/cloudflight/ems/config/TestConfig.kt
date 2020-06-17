package io.cloudflight.ems.config

import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.UserRole
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.repository.UserRoleRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder
import javax.transaction.Transactional

@TestConfiguration
class TestConfig(
    val userRepository: UserRepository,
    val userRoleRepository: UserRoleRepository,
    val passwordEncoder: PasswordEncoder
) {

    @Bean
    @Transactional
    fun initService() {
        val userRole =
            userRoleRepository.findOneByName("administrator")
                ?: userRoleRepository.save(UserRole(1, "administrator"));
        userRepository.findOneByEmail("admin")
            ?: run {
                val adminPassword = "Adm1"
                userRepository.save(
                    User(
                        id = 1,
                        email = "admin",
                        password = passwordEncoder.encode(adminPassword),
                        name = "admin",
                        surname = "admin",
                        userRole = userRole
                    )
                )
            }
    }
}
