package io.cloudflight.jems.server.programme.entity.costoption

import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "programme_unit_cost")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProgrammeUnitCostTranslEntity.fetchWithTranslations",
        attributeNodes = [NamedAttributeNode(value = "translatedValues")]
    )
)
data class ProgrammeUnitCostEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // name, description, type
    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        mappedBy = "translationId.programmeUnitCostId",
        fetch = FetchType.EAGER
    )
    var translatedValues: MutableSet<ProgrammeUnitCostTranslEntity> = mutableSetOf(),

    @field:NotNull
    var costPerUnit: BigDecimal,

    @field:NotNull
    var isOneCostCategory: Boolean,

    @OneToMany(mappedBy = "programmeUnitCostId", cascade = [CascadeType.ALL], orphanRemoval = true)
    var categories: MutableSet<ProgrammeUnitCostBudgetCategoryEntity> = mutableSetOf()
)
