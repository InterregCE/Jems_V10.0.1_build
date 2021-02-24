package io.cloudflight.jems.server.programme.entity.costoption

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "programme_lump_sum")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProgrammeLumpSumTranslEntity.fetchWithTranslations",
        attributeNodes = [NamedAttributeNode(value = "translatedValues")]
    )
)
data class ProgrammeLumpSumEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // name, description
    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        mappedBy = "translationId.programmeLumpSumId",
        fetch = FetchType.EAGER
    )
    var translatedValues: MutableSet<ProgrammeLumpSumTranslEntity> = mutableSetOf(),

    @field:NotNull
    var cost: BigDecimal,

    @field:NotNull
    var splittingAllowed: Boolean,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var phase: ProgrammeLumpSumPhase,

    @OneToMany(mappedBy = "programmeLumpSumId", cascade = [CascadeType.ALL], orphanRemoval = true)
    var categories: MutableSet<ProgrammeLumpSumBudgetCategoryEntity> = mutableSetOf(),
)
