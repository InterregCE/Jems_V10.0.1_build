package io.cloudflight.jems.server.programme.service.fund.get_fund

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetFundInteractor {

    fun getFunds(): List<ProgrammeFund>

}
