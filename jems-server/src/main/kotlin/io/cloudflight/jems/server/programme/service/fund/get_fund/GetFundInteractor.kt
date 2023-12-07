package io.cloudflight.jems.server.programme.service.fund.get_fund

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

interface GetFundInteractor {

    fun getFunds(): List<ProgrammeFund>

}
