package io.cloudflight.jems.api.project.dto

data class ProjectPartnerStateAidDTO (
    val answer1: Boolean? = null,
    val justification1: Set<InputTranslation> = emptySet(),
    val answer2: Boolean? = null,
    val justification2: Set<InputTranslation> = emptySet(),
    val answer3: Boolean? = null,
    val justification3: Set<InputTranslation> = emptySet(),
    val answer4: Boolean? = null,
    val justification4: Set<InputTranslation> = emptySet(),
)
