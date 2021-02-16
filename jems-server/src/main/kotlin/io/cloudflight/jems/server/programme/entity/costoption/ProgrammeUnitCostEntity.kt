package io.cloudflight.jems.server.programme.entity.costoption

import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "programme_unit_cost")
data class ProgrammeUnitCostEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    var name: String,

    var description: String? = null,

    @field:NotNull
    var type: String,

    @field:NotNull
    var costPerUnit: BigDecimal,

    @field:NotNull
    var isOneCostCategory: Boolean,

    @OneToMany(mappedBy = "programmeUnitCostId", cascade = [CascadeType.ALL], orphanRemoval = true)
    var categories: MutableSet<ProgrammeUnitCostBudgetCategoryEntity> = mutableSetOf()
)
