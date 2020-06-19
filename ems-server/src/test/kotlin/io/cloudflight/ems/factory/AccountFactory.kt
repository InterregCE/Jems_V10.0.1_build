package io.cloudflight.ems.factory

import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.entity.AccountRole
import io.cloudflight.ems.repository.AccountRepository
import io.cloudflight.ems.repository.AccountRoleRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class AccountFactory(
    val accountRepository: AccountRepository,
    val accountRoleRepository: AccountRoleRepository,
    val passwordEncoder: PasswordEncoder
) {
    val adminRole: AccountRole = saveAdminRole()

    val adminAccount: Account = saveAdminAccount()

    @Transactional
    fun saveAdminRole(): AccountRole {
        return accountRoleRepository.findOneByName("administrator")
            ?: accountRoleRepository.save(AccountRole(1, "administrator"))
    }

    @Transactional
    fun saveAdminAccount(): Account {
        return accountRepository.findOneByEmail("admin")
            ?: accountRepository.save(
                Account(
                    id = 1,
                    email = "admin",
                    password = passwordEncoder.encode("Adm1"),
                    name = "admin",
                    surname = "admin",
                    accountRole = adminRole
                )
            )
    }

    @Transactional
    fun saveAdminAccount(email: String): Account {
        return accountRepository.save(
            Account(
                id = null,
                email = email,
                password = passwordEncoder.encode(email),
                name = email,
                surname = email,
                accountRole = adminRole
            )
        )
    }
}
