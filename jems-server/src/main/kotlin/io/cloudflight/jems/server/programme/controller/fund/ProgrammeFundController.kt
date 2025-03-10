package io.cloudflight.jems.server.programme.controller.fund

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.fund.ProgrammeFundApi
import io.cloudflight.jems.server.programme.service.fund.getFund.GetFundInteractor
import io.cloudflight.jems.server.programme.service.fund.updateFunds.UpdateFundsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeFundController(
    private val getFund: GetFundInteractor,
    private val updateFunds: UpdateFundsInteractor,
) : ProgrammeFundApi {

    override fun getProgrammeFundList(): List<ProgrammeFundDTO> =
        getFund.getFunds().toDto()

    override fun updateProgrammeFundList(funds: Set<ProgrammeFundDTO>): List<ProgrammeFundDTO> =
        updateFunds.update(funds.toModelList()).toDto()

}
