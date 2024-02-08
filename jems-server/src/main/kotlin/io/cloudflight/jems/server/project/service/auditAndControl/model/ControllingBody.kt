package io.cloudflight.jems.server.project.service.auditAndControl.model

enum class ControllingBody {
    Controller,
    NationalApprobationBody,
    RegionalApprobationBody,
    JS,
    MA,
    MABAF,
    NA,
    GoA,
    AA,
    EC,
    ECA,
    OLAF;

    companion object  {
        val ecOrEcaOrOlafInvestigations = setOf(EC, ECA, OLAF)
    }

    fun isAaAudit() = this == AA

    fun isEcOrEcaOrOlafInvestigation() = this in ecOrEcaOrOlafInvestigations

}
