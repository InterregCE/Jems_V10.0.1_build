package io.cloudflight.jems.server.project.service.contracting.model

import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import java.math.BigDecimal

data class ContractingDimensionCode(
    val id: Long,
    val projectId: Long,
    val programmeObjectiveDimension: ProgrammeObjectiveDimension,
    val dimensionCode: String,
    val projectBudgetAmountShare: BigDecimal
)
