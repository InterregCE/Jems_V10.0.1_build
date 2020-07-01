package io.cloudflight.ems.factory

import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.entity.AccountRole
import io.cloudflight.ems.repository.AccountRepository
import io.cloudflight.ems.repository.AccountRoleRepository
import io.cloudflight.ems.security.ADMINISTRATOR
import io.cloudflight.ems.security.APPLICANT_USER
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

    val applicantAccount: Account = saveApplicantUser(APPLICANT_USER_EMAIL)

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

    fun saveApplicantUser(email: String): Account {
        val programmeRole = saveRole(APPLICANT_USER)
        return saveAccount(email, programmeRole)
    }

    companion object {
        const val ADMINISTRATOR_EMAIL = "administrator@email.com"
        const val APPLICANT_USER_EMAIL = "applicant_user@email.com"
    }
}
