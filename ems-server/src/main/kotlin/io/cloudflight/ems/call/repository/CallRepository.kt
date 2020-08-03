package io.cloudflight.ems.call.repository

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.call.entity.Call
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CallRepository: PagingAndSortingRepository<Call, Long> {

    fun findAllByStatus(status: CallStatus, pageable: Pageable): Page<Call>

    fun findOneByName(name: String): Call?

}
