package io.cloudflight.jems.server.programme.controller

import io.cloudflight.jems.api.programme.ProgrammeFundApi
import io.cloudflight.jems.api.programme.dto.InputProgrammeFundWrapper
import io.cloudflight.jems.api.programme.dto.ProgrammeFundOutputDTO
import io.cloudflight.jems.server.programme.service.ProgrammeFundService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeFundController(
    private val programmeFundService: ProgrammeFundService
) : ProgrammeFundApi {

    override fun getProgrammeFundList(pageable: Pageable): List<ProgrammeFundOutputDTO> {
        return programmeFundService.get()
    }

    override fun updateProgrammeFundList(fundData: InputProgrammeFundWrapper): List<ProgrammeFundOutputDTO> {
        return programmeFundService.update(fundData.funds)
    }

}
