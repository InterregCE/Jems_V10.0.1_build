package io.cloudflight.jems.server.project.service.contracting.model.reporting

enum class ContractingDeadlineType {
    Content,
    Finance,
    Both;

    fun hasWorkPlan() = this == Content || this == Both
}
