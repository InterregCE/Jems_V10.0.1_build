package io.cloudflight.jems.server.project.service.contracting.model

import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureLastPaymentDate
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import java.time.LocalDate

data class ProjectContractingMonitoring(
    val projectId: Long,
    val startDate: LocalDate? = null,
    var endDate: LocalDate? = null,
    val closureDate: LocalDate? = null, // TODO why default value provided?
    var lastPaymentDates: List<ContractingClosureLastPaymentDate>,

    val typologyProv94: ContractingMonitoringExtendedOption,
    val typologyProv94Comment: String? = null,
    val typologyProv95: ContractingMonitoringExtendedOption,
    val typologyProv95Comment: String? = null,

    val typologyStrategic: ContractingMonitoringOption,
    val typologyStrategicComment: String? = null,
    val typologyPartnership: ContractingMonitoringOption,
    val typologyPartnershipComment: String? = null,

    val addDates: List<ProjectContractingMonitoringAddDate>,
    val dimensionCodes: List<ContractingDimensionCode>,
    var fastTrackLumpSums: List<ProjectLumpSum>? = emptyList()

) {
    fun getDiff(old: ProjectContractingMonitoring? = null): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()

        if (old == null || startDate != old.startDate)
            changes["startDate"] = Pair(old?.startDate, startDate)

        if (old == null || typologyProv94 != old.typologyProv94)
            changes["typologyProv94"] = Pair(old?.typologyProv94, typologyProv94)
        if (old == null || typologyProv95 != old.typologyProv95)
            changes["typologyProv95"] = Pair(old?.typologyProv95, typologyProv95)
        if (old == null || typologyStrategic != old.typologyStrategic)
            changes["typologyStrategic"] = Pair(old?.typologyStrategic, typologyStrategic)
        if (old == null || typologyPartnership != old.typologyPartnership)
            changes["typologyPartnership"] = Pair(old?.typologyPartnership, typologyPartnership)

        val oldAddDates = old?.addDates?.sortedBy { it.number }?.map { it.entryIntoForceDate }
        val newAddDates = addDates.sortedBy { it.number }.map { it.entryIntoForceDate }
        if (newAddDates != oldAddDates)
            changes["addSubContractDates"] = Pair(
                oldAddDates?.map { it },
                newAddDates.map { it }
            )

        return changes
    }
}
