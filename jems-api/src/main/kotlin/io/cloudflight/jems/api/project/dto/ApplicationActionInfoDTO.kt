package io.cloudflight.jems.api.project.dto

import io.swagger.annotations.ApiModel
import java.time.LocalDate

@ApiModel(value = "ApplicationActionInfoDTO")
data class ApplicationActionInfoDTO(
    val note: String?,
    val date: LocalDate?,
    val entryIntoForceDate: LocalDate?
)
