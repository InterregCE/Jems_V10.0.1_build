package io.cloudflight.jems.server.programme.service.fund

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

interface ProgrammeFundPersistence {

    fun getMax20Funds(): List<ProgrammeFund>

    fun getById(fundId: Long): ProgrammeFund

    fun updateFunds(toDeleteIds: Set<Long>, funds: Set<ProgrammeFund>): List<ProgrammeFund>

}
