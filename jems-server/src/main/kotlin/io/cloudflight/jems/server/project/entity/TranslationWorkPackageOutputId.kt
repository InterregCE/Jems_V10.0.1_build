package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.programme.dto.SystemLanguage
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutputId
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

/**
 * ID for the use to embed the PK for translation tables for WorkPackage Output relations.
 */
@Embeddable
data class TranslationWorkPackageOutputId(

    @field:NotNull
    val workPackageOutputId: WorkPackageOutputId,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

) : Serializable
