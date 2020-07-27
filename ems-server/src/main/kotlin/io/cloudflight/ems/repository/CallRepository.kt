package io.cloudflight.ems.repository

import io.cloudflight.ems.entity.Call
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CallRepository: PagingAndSortingRepository<Call, Long> {

    fun findOneById(id: Long): Call?

}
