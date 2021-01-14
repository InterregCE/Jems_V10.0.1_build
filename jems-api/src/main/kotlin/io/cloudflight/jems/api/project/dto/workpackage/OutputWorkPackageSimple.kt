package io.cloudflight.jems.api.project.dto.workpackage

import io.cloudflight.jems.api.project.dto.InputTranslation

data class OutputWorkPackageSimple (
    val id: Long,
    val number: Int?,
    val name: Set<InputTranslation> = emptySet()
)
