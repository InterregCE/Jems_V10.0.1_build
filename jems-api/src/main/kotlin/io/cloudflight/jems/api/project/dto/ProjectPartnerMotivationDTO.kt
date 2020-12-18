package io.cloudflight.jems.api.project.dto

data class ProjectPartnerMotivationDTO (

    val organizationRelevance: Set<InputTranslation> = emptySet(),
    val organizationRole: Set<InputTranslation> = emptySet(),
    val organizationExperience: Set<InputTranslation> = emptySet()

)
