package io.cloudflight.jems.api.project.dto.contracting

import io.cloudflight.jems.api.project.dto.lumpsum.ProjectLumpSumDTO
import java.time.LocalDate

data class ProjectContractingMonitoringDTO(
    val projectId: Long,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,

    val typologyProv94: ContractingMonitoringExtendedOptionDTO? = null,
    val typologyProv94Comment: String? = null,
    val typologyProv95: ContractingMonitoringExtendedOptionDTO? = null,
    val typologyProv95Comment: String? = null,

    val typologyStrategic: ContractingMonitoringOptionDTO? = null,
    val typologyStrategicComment: String? = null,
    val typologyPartnership: ContractingMonitoringOptionDTO? = null,
    val typologyPartnershipComment: String? = null,

    val addDates: List<ProjectContractingMonitoringAddDateDTO>,
    val dimensionCodes: List<ContractingDimensionCodeDTO>,
    val fastTrackLumpSums: List<ProjectLumpSumDTO>? = emptyList()
)
