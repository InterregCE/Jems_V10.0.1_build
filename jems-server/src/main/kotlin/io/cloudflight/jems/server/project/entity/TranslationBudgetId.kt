package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.api.programme.dto.SystemLanguage
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

/**
 * ID for the use to embed the PK for translation tables for Budget relations.
 */
@Embeddable
data class TranslationBudgetId(

    @Column(name = "budget_id")
    @field:NotNull
    val budgetId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val language: SystemLanguage

) : Serializable
