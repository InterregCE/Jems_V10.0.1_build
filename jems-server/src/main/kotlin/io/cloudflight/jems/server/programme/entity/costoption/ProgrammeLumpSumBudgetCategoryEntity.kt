package io.cloudflight.jems.server.programme.entity.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "programme_lump_sum_budget_category")
data class ProgrammeLumpSumBudgetCategoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val programmeLumpSumId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val category: BudgetCategory,

)
