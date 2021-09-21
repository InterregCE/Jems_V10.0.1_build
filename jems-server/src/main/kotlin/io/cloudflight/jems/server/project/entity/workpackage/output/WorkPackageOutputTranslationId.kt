package io.cloudflight.jems.server.project.entity.workpackage.output

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

/**
 * ID for the use to embed the PK for translation tables for WorkPackage Output relations.
 */
@Embeddable
data class WorkPackageOutputTranslationId(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        JoinColumn(name="work_package_id", referencedColumnName="work_package_id"),
        JoinColumn(name="output_number", referencedColumnName="output_number")
    )
    @field:NotNull
    override val sourceEntity: WorkPackageOutputEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    override val language: SystemLanguage

) : TranslationId<WorkPackageOutputEntity>(sourceEntity, language)
