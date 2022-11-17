package io.cloudflight.jems.server.programme.controller.typologyerrors

import io.cloudflight.jems.api.programme.dto.typologyerrors.TypologyErrorsDTO
import io.cloudflight.jems.api.programme.dto.typologyerrors.TypologyErrorsUpdateDTO
import io.cloudflight.jems.api.programme.typologyerrors.ProgrammeTypologyErrorsApi
import io.cloudflight.jems.server.programme.service.typologyerrors.GetTypologyErrorsInteractor
import io.cloudflight.jems.server.programme.service.typologyerrors.UpdateTypologyErrorsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeTypologyErrorsController(
    private val getTypologyErrors: GetTypologyErrorsInteractor,
    private val updateTypologyErrors: UpdateTypologyErrorsInteractor,
) : ProgrammeTypologyErrorsApi {

    override fun getTypologyErrors(): List<TypologyErrorsDTO> {
        return getTypologyErrors.getTypologyErrors().toDto()
    }

    override fun updateTypologyErrors(typologyErrors: TypologyErrorsUpdateDTO): List<TypologyErrorsDTO> {
        return updateTypologyErrors.updateTypologyErrors(typologyErrors.toDeleteIds, typologyErrors.toPersist.toModel()).toDto()
    }
}
