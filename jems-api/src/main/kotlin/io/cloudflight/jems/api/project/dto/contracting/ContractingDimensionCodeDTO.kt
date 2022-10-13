package io.cloudflight.jems.api.project.dto.contracting

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectiveDimensionDTO
import java.math.BigDecimal

data class ContractingDimensionCodeDTO(
    val id: Long,
    val projectId: Long,
    val programmeObjectiveDimension: ProgrammeObjectiveDimensionDTO,
    val dimensionCode: String,
    val projectBudgetAmountShare: BigDecimal
)
