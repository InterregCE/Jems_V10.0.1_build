package io.cloudflight.ems.call.repository

import io.cloudflight.ems.api.call.dto.CallStatus
import io.cloudflight.ems.call.entity.Call
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
interface CallRepository: PagingAndSortingRepository<Call, Long> {

    fun findAllByStatusAndEndDateAfter(status: CallStatus, endDateAfter: ZonedDateTime, pageable: Pageable): Page<Call>

    fun findOneByName(name: String): Call?

}
