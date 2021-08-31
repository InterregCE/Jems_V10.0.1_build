package io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinColumns
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
data class WorkPackageActivityDeliverableTranslationId(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        JoinColumn(name = "work_package_id", referencedColumnName = "work_package_id"),
        JoinColumn(name = "activity_number", referencedColumnName = "activity_number"),
        JoinColumn(name = "deliverable_number", referencedColumnName = "deliverable_number")
    )
    @field:NotNull
    override val sourceEntity: WorkPackageActivityDeliverableEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    override val language: SystemLanguage

) : TranslationId<WorkPackageActivityDeliverableEntity>(sourceEntity, language)
