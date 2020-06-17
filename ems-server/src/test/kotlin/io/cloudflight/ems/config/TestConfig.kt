package io.cloudflight.ems.config

import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.entity.AccountRole
import io.cloudflight.ems.repository.AccountRepository
import io.cloudflight.ems.repository.AccountRoleRepository
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder
import javax.transaction.Transactional

@TestConfiguration
class TestConfig(
    val accountRepository: AccountRepository,
    val accountRoleRepository: AccountRoleRepository,
    val passwordEncoder: PasswordEncoder
) {

    @Bean
    @Transactional
    fun initService() {
        val userRole =
            accountRoleRepository.findOneByName("administrator")
                ?: accountRoleRepository.save(AccountRole(1, "administrator"));
        accountRepository.findOneByEmail("admin")
            ?: run {
                val adminPassword = "Adm1"
                accountRepository.save(
                    Account(
                        id = 1,
                        email = "admin",
                        password = passwordEncoder.encode(adminPassword),
                        name = "admin",
                        surname = "admin",
                        accountRole = userRole
                    )
                )
            }
    }
}
