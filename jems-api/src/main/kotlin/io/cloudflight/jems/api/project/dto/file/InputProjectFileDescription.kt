package io.cloudflight.jems.api.project.dto.file

import javax.validation.constraints.Size

data class InputProjectFileDescription (

    @field:Size(max = 100, message = "project.file.description.size.too.long")
    val description: String?

)
