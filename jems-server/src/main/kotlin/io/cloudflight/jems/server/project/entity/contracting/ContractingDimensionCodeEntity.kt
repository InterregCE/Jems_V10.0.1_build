package io.cloudflight.jems.server.project.entity.contracting

import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "project_contracting_dimension_code")
class ContractingDimensionCodeEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @field: NotNull
    val projectId: Long,

    @Enumerated(EnumType.STRING)
    @field: NotNull
    val programmeObjectiveDimension: ProgrammeObjectiveDimension,

    @field: NotNull
    val dimensionCode: String,

    @field: NotNull
    val projectBudgetAmountShare: BigDecimal
)
