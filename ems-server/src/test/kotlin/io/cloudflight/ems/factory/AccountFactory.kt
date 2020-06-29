package io.cloudflight.ems.factory

import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.entity.AccountRole
import io.cloudflight.ems.repository.AccountRepository
import io.cloudflight.ems.repository.AccountRoleRepository
import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.security.PROGRAMME_USER
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class AccountFactory(
    val accountRepository: AccountRepository,
    val accountRoleRepository: AccountRoleRepository,
    val passwordEncoder: PasswordEncoder
) {

    val adminAccount: Account = saveAdminAccount(ADMINISTRATOR_EMAIL)

    val programmeAccount: Account = saveProgrammeUser(PROGRAMME_USER_EMAIL)

    @Transactional
    fun saveRole(roleName: String): AccountRole {
        return accountRoleRepository.findOneByName(roleName)
            ?: accountRoleRepository.save(AccountRole(null, roleName))
    }

    @Transactional
    fun saveAccount(email: String, role: AccountRole): Account {
        return accountRepository.findOneByEmail(email)
            ?: accountRepository.save(
                Account(
                    id = null,
                    email = email,
                    password = passwordEncoder.encode(email),
                    name = email,
                    surname = email,
                    accountRole = role
                )
            )
    }

    fun saveAdminAccount(email: String): Account {
        val adminRole: AccountRole = saveRole(ADMINISTRATOR)
        return saveAccount(email, adminRole)
    }

    fun saveProgrammeUser(email: String): Account {
        val programmeRole = saveRole(PROGRAMME_USER)
        return saveAccount(email, programmeRole)
    }

    companion object {
        const val ADMINISTRATOR_EMAIL = "administrator@email.com"
        const val PROGRAMME_USER_EMAIL = "programme_user@email.com"
    }
}
