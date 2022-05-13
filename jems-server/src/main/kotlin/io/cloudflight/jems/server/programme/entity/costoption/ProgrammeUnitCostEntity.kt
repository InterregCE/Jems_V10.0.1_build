package io.cloudflight.jems.server.programme.entity.costoption

import java.math.BigDecimal
import javax.persistence.*
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
