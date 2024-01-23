package io.cloudflight.jems.api.project.dto.contracting

import io.cloudflight.jems.api.project.dto.lumpsum.ProjectLumpSumDTO
import java.time.LocalDate

data class ProjectContractingMonitoringDTO(
    val projectId: Long,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val closureDate: LocalDate? = null,
    val partnerPaymentDates: List<ContractingMonitoringPartnerLastPaymentDTO>,

    val entryIntoForceDate: LocalDate? = null,
    val entryIntoForceComment: String? = null,

    val typologyProv94: ContractingMonitoringExtendedOptionDTO,
    val typologyProv94Comment: String? = null,
    val typologyProv95: ContractingMonitoringExtendedOptionDTO,
    val typologyProv95Comment: String? = null,

    val typologyStrategic: ContractingMonitoringOptionDTO,
    val typologyStrategicComment: String? = null,
    val typologyPartnership: ContractingMonitoringOptionDTO,
    val typologyPartnershipComment: String? = null,

    val addDates: List<ProjectContractingMonitoringAddDateDTO>,
    val dimensionCodes: List<ContractingDimensionCodeDTO>,
    val fastTrackLumpSums: List<ProjectLumpSumDTO>? = emptyList(),
)
