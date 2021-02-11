package io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Embeddable
data class WorkPackageActivityDeliverableTranslationId(

    @Embedded
    val deliverableId: WorkPackageActivityDeliverableId,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

) : Serializable
