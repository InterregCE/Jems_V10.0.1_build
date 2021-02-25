package io.cloudflight.jems.server.programme.controller.fund

import io.cloudflight.jems.api.programme.fund.ProgrammeFundApi
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.server.programme.service.fund.get_fund.GetFundInteractor
import io.cloudflight.jems.server.programme.service.fund.update_fund.UpdateFundInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeFundController(
    private val getFundInteractor: GetFundInteractor,
    private val updateFundInteractor: UpdateFundInteractor,
) : ProgrammeFundApi {

    override fun getProgrammeFundList(): List<ProgrammeFundDTO> =
        getFundInteractor.getFunds().toDto()

    override fun updateProgrammeFundList(funds: Set<ProgrammeFundDTO>): List<ProgrammeFundDTO> =
        updateFundInteractor.updateFunds(funds.toModel()).toDto()

}
