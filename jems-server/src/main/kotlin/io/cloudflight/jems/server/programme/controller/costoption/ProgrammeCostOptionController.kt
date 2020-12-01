package io.cloudflight.jems.server.programme.controller.costoption

import io.cloudflight.jems.api.programme.ProgrammeCostOptionApi
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.server.programme.service.costoption.create_lump_sum.CreateLumpSumInteractor
import io.cloudflight.jems.server.programme.service.costoption.delete_lump_sum.DeleteLumpSumInteractor
import io.cloudflight.jems.server.programme.service.costoption.get_lump_sum.GetLumpSumInteractor
import io.cloudflight.jems.server.programme.service.costoption.update_lump_sum.UpdateLumpSumInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeCostOptionController(
    private val getLumpSum: GetLumpSumInteractor,
    private val createLumpSum: CreateLumpSumInteractor,
    private val updateLumpSum: UpdateLumpSumInteractor,
    private val deleteLumpSum: DeleteLumpSumInteractor,
) : ProgrammeCostOptionApi {

    override fun getProgrammeLumpSums(pageable: Pageable): Page<ProgrammeLumpSumDTO> =
        getLumpSum.getLumpSums(pageable).map { it.toDto() }

    override fun createProgrammeLumpSum(lumpSum: ProgrammeLumpSumDTO): ProgrammeLumpSumDTO =
        createLumpSum.createLumpSum(lumpSum.toModel()).toDto()

    override fun updateProgrammeLumpSum(lumpSum: ProgrammeLumpSumDTO): ProgrammeLumpSumDTO =
        updateLumpSum.updateLumpSum(lumpSum.toModel()).toDto()

    override fun deleteProgrammeLumpSum(lumpSumId: Long) =
        deleteLumpSum.deleteLumpSum(lumpSumId)

}
