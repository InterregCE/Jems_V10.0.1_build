package io.cloudflight.jems.server.programme.service.fund.model

enum class ProgrammeFundType(val key: String) {
    ERDF("ERDF"),
    IPA_III_CBC("IPA III CBC"),
    NEIGHBOURHOOD_CBC("Neighbourhood CBC"),
    IPA_III("IPA III"),
    NDICI("NDICI"),
    OCTP("OCTP"),
    INTERREG_FUNDS("Interreg Funds"),
    OTHER("Other");

    companion object {
        fun from(s: String): ProgrammeFundType? = values().find { it.key == s }
    }
}
