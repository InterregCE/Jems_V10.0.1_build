package io.cloudflight.jems.server.programme.service.fund.model

data class ProgrammeFund(
    val id: Long = 0,
    val selected: Boolean,
    val translatedValues: Set<ProgrammeFundTranslatedValue> = emptySet(),
) {
    fun selectionChanged(other: ProgrammeFund?) =
        if (other != null) selected != other.selected else true
}
