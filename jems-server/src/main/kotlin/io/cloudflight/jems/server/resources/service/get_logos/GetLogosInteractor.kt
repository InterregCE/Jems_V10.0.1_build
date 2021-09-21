package io.cloudflight.jems.server.resources.service.get_logos

import io.cloudflight.jems.api.common.dto.LogoDTO
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.resources.repository.GetLogosPersistence
import org.springframework.stereotype.Service

@Service
class GetLogosInteractor(private val logosPersistence: GetLogosPersistence) : GetLogos {

    @ExceptionWrapper(GetLogoFailed::class)
    override fun getLogos(): List<LogoDTO> = logosPersistence.getLogos()
}
