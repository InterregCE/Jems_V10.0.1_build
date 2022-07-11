package io.cloudflight.jems.server.project.service.contracting.model

import java.time.LocalDate

data class ProjectContractingMonitoring(
    val projectId: Long,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,

    val typologyProv94: ContractingMonitoringExtendedOption? = null,
    val typologyProv94Comment: String? = null,
    val typologyProv95: ContractingMonitoringExtendedOption? = null,
    val typologyProv95Comment: String? = null,

    val typologyStrategic: ContractingMonitoringOption? = null,
    val typologyStrategicComment: String? = null,
    val typologyPartnership: ContractingMonitoringOption? = null,
    val typologyPartnershipComment: String? = null,

    val addDates: List<ProjectContractingMonitoringAddDate>

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

        val oldAddDates = old?.addDates?.sortedBy { it.number }
        val newAddDates = addDates.sortedBy { it.number }
        if (newAddDates != oldAddDates)
            changes["addSubContractDates"] = Pair(
                oldAddDates?.map { it.entryIntoForceDate },
                newAddDates.map { it.entryIntoForceDate }
            )

        return changes
    }
}
