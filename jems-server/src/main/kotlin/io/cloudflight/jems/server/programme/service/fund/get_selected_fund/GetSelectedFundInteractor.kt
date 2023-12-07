package io.cloudflight.jems.server.programme.service.fund.get_selected_fund

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType

interface GetSelectedFundInteractor {

    fun getAvailableFunds(): List<ProgrammeFundType>

}
