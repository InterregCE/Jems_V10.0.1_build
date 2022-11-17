package io.cloudflight.jems.server.programme.entity.costoption

import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Entity
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

    val projectId: Long?,

    @field:NotNull
    var isOneCostCategory: Boolean,

    @field:NotNull
    var costPerUnit: BigDecimal,

    var costPerUnitForeignCurrency: BigDecimal?,

    var foreignCurrencyCode: String?,

    // name, description, type
    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        mappedBy = "translationId.programmeUnitCostId"
    )
    var translatedValues: MutableSet<ProgrammeUnitCostTranslEntity> = mutableSetOf(),

    @OneToMany(mappedBy = "programmeUnitCostId", cascade = [CascadeType.ALL], orphanRemoval = true)
    var categories: MutableSet<ProgrammeUnitCostBudgetCategoryEntity> = mutableSetOf()

)
