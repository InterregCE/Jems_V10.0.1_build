package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.AccountRole
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRoleRepository : PagingAndSortingRepository<AccountRole, Long> {

    fun findOneByName(name: String): AccountRole?

}
