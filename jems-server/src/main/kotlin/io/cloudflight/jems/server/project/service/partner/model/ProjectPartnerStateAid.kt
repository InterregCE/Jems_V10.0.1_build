package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivitySummary

data class ProjectPartnerStateAid(
    val answer1: Boolean?,
    val justification1: Set<InputTranslation> = emptySet(),
    val answer2: Boolean?,
    val justification2: Set<InputTranslation> = emptySet(),
    val answer3: Boolean?,
    val justification3: Set<InputTranslation> = emptySet(),
    val answer4: Boolean?,
    val justification4: Set<InputTranslation> = emptySet(),
    val activities: List<WorkPackageActivitySummary>? = emptyList(),
    val stateAidScheme: ProgrammeStateAid? = null
)
