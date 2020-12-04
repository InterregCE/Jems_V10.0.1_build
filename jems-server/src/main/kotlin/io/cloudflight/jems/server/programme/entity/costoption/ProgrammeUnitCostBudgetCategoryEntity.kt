package io.cloudflight.jems.server.programme.entity.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "programme_unit_cost_budget_category")
data class ProgrammeUnitCostBudgetCategoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val programmeUnitCostId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val category: BudgetCategory,

)
