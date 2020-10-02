package io.cloudflight.ems.api.dto

import javax.validation.constraints.Size

data class InputProjectFileDescription (

    @field:Size(max = 100, message = "project.file.description.size.too.long")
    val description: String?

)
