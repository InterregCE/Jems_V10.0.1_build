package io.cloudflight.jems.server.project.service.contracting.model.reporting

enum class ContractingDeadlineType {
    Content,
    Finance,
    Both;

    fun hasContent() = this == Content || this == Both
    fun hasFinance() = this == Finance || this == Both

}
