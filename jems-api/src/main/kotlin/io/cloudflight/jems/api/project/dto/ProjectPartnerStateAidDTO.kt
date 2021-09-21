package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidDTO
import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivitySummaryDTO

data class ProjectPartnerStateAidDTO (
    val answer1: Boolean?,
    val justification1: Set<InputTranslation> = emptySet(),
    val answer2: Boolean?,
    val justification2: Set<InputTranslation> = emptySet(),
    val answer3: Boolean?,
    val justification3: Set<InputTranslation> = emptySet(),
    val answer4: Boolean?,
    val justification4: Set<InputTranslation> = emptySet(),
    val activities: List<WorkPackageActivitySummaryDTO>? = emptyList(),
    val stateAidScheme: ProgrammeStateAidDTO? = null
)
