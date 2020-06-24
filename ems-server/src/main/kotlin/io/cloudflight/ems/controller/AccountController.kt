package io.cloudflight.ems.controller

import io.cloudflight.ems.api.AccountApi
import io.cloudflight.ems.api.dto.InputAccount
import io.cloudflight.ems.api.dto.OutputAccount
import io.cloudflight.ems.service.AccountService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    val accountService: AccountService
) : AccountApi {

    override fun list(pageable: Pageable): Page<OutputAccount> {
        return accountService.findAll(pageable = pageable)
    }

    override fun createUser(account: InputAccount): OutputAccount {
        return accountService.create(account)
    }
}
