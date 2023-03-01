package io.cloudflight.jems.api.project.dto.workpackage

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO

data class ProjectWorkPackageDTO(
    val id: Long,
    val deactivated: Boolean,
    val workPackageNumber: Int,
    val name: Set<InputTranslation> = emptySet(),
    val activities: List<WorkPackageActivityDTO> = emptyList(),
    val outputs: List<WorkPackageOutputDTO> = emptyList(),
)
