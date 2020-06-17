package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.Account
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : PagingAndSortingRepository<Account, Long> {

    fun findOneByEmail(email: String): Account?

}
