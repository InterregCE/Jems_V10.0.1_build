package io.cloudflight.jems.server.project.service.contracting.model

enum class ContractingMonitoringExtendedOption {
    Yes,
    No,
    Partly;

    fun isNo() = this == No

}
