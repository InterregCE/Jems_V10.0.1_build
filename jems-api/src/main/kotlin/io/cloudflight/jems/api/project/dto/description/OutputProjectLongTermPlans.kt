package io.cloudflight.jems.api.project.dto.description

import io.cloudflight.jems.api.project.dto.InputTranslation

/**
 * C8
 */
data class OutputProjectLongTermPlans(
    val projectOwnership: Set<InputTranslation> = emptySet(),
    val projectDurability: Set<InputTranslation> = emptySet(),
    val projectTransferability: Set<InputTranslation> = emptySet()
)
