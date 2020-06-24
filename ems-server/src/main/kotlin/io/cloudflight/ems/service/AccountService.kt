package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputAccount
import io.cloudflight.ems.api.dto.OutputAccount
import io.cloudflight.ems.dto.UserWithCredentials
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AccountService {

    fun findOneByEmail(email: String): UserWithCredentials?

    fun getByEmail(email: String): OutputAccount?

    fun findAll(pageable: Pageable): Page<OutputAccount>

    fun create(account: InputAccount): OutputAccount

}
